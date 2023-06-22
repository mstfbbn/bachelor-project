package me.biabani.encryption.searchable.song;

import javafx.util.Pair;
import me.biabani.encryption.searchable.common.EncryptionUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Index {

    /* This is equal to 'n' */
    public static final int BLOCK_SIZE_IN_BITS = 128;

    /**
     *  we choose 'm' to be half of 'n' for simplicity and a low probability of false positive answers according to
     * one before last paragraph of Abstract.
     */
    public static final int m = BLOCK_SIZE_IN_BITS / 2;
    public static final int BLOCK_SIZE_IN_BYTES = BLOCK_SIZE_IN_BITS / 8;

    // todo should be saved
    private static final byte[] iv = EncryptionUtil.aesGenerateRandomIv();
    private static final String userKey = "abcdefghi";

    /**
     * content of a file can be mapped to a list of words. before anything, we have to break a file into a list of words.
     * Therefore, this function is the first thing to do at encryption time.
     *
     * @return list of words in file
     */
    private List<String> getWordsOfFile(File file) throws IOException {

        List<String> lines = Files.readAllLines(file.toPath());
        List<String[]> wordsPerLine = lines.stream().map(item -> item.split(" ")).collect(Collectors.toList());
        List<String> totalWords = new ArrayList<>();
        wordsPerLine.forEach(item -> totalWords.addAll(Arrays.asList(item)));
        return totalWords;
    }

    /**
     * Src: '2 Searching on Encrypted Data'
     * This method changes words to fixed-size byte arrays. if the word is smaller in size, we use padding.
     * If the word is larger in size, we break it into pieces.
     *
     * @param words List of words in a file which is the output of {@link #getWordsOfFile}
     * @return list of fixed-size byte arrays
     */
    private List<byte[]> mapWordsToBlocks(List<String> words) {

        List<byte[]> byteList = new ArrayList<>();
        words.forEach(word -> {
            byte[] wordBytes = word.getBytes(StandardCharsets.US_ASCII);
            if (wordBytes.length < BLOCK_SIZE_IN_BYTES) {
                byteList.add(padWordToBlockSize(wordBytes));
            } else if (wordBytes.length == BLOCK_SIZE_IN_BYTES) {
                byteList.add(wordBytes);
            } else {
                for (int i = 0; i < Math.ceil((double) wordBytes.length / BLOCK_SIZE_IN_BYTES); i++) {
                    if ((i + 1) * BLOCK_SIZE_IN_BYTES < wordBytes.length) {
                        byteList.add(Arrays.copyOfRange(wordBytes, i * BLOCK_SIZE_IN_BYTES, (i + 1) * BLOCK_SIZE_IN_BYTES));
                    } else {
                        byteList.add(padWordToBlockSize(Arrays.copyOfRange(wordBytes, i * BLOCK_SIZE_IN_BYTES, wordBytes.length)));
                    }
                }
            }
        });

        return byteList;
    }

    /**
     * As mentioned in '4.3 Scheme III', we should encrypt each word to enable support for hidden searches.
     * For more info, this function returns X(i) from W(i)
     *
     * @param key the key for encryption
     * @param plainText list of blocks
     * @return list of encrypted texts
     */
    private List<byte[]> encryptPlainWords(byte[] key, List<byte[]> plainText) {

        return plainText.stream().map(item -> {
            try {
                return EncryptionUtil.aesEncryptECB(key, item);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }).collect(Collectors.toList());
    }

    /**
     * This function is implementing the pseudorandom function 'G' which is mentioned in second paragraph of
     * '4.1 Scheme I: The Basic Scheme'. it means, it generate S(i).
     * @param userKey the key for encryption, provided by user.
     * @param index i
     * @return S(i)
     */
    private byte[] getSequentialRandomFunctionG(String userKey, Integer index) {

        // string to AES CBC encrypted. we know that by having the fixed IV, we always get the same result.
        String encryptedAndBase64 = EncryptionUtil.aesEncryptCBC(userKey.getBytes(StandardCharsets.UTF_8), iv, " ".getBytes(StandardCharsets.UTF_8));

        // one of the ways to get random S(i) is to rotate based on index
        return (encryptedAndBase64.substring(index) + encryptedAndBase64.substring(0, index)).getBytes(StandardCharsets.UTF_8);
    }

    /**
     *
     * @param userKey key provided by user
     * @param rawMessage S(i)
     * @return Fk(S(i))
     */
    private byte[] getSequentialRandomFunctionF(String userKey, byte[] rawMessage) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        return EncryptionUtil.aesEncryptECB(userKey.getBytes(StandardCharsets.UTF_8), rawMessage);
    }

    /**
     * As mentioned in '4.1 Scheme I: The Basic Scheme' second paragraph, T(i) is generated using concatenation of
     * s(i) and F(s(i))
     *
     * @param userKey encryption key provided by user
     * @param index i
     * @return T(i)
     */
    private byte[] getT(String userKey, Integer index) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        byte[] g = getSequentialRandomFunctionG(userKey, index);
        byte[] f = getSequentialRandomFunctionF(userKey, g);
        byte[] t = new byte[g.length + f.length];
        System.arraycopy(g, 0, t, 0, g.length);
        System.arraycopy(f, 0, t, g.length, f.length);
        return t;
    }

    /**
     * this function does the encryption process by XORing X(i) with T(i)
     *
     * @param blocks
     * @param userKey
     * @return
     */
    private List<byte[]> xorEachBlockWithT(List<byte[]> blocks, String userKey) throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        List<byte[]> result = new ArrayList<>();
        for (int i = 0; i < blocks.size(); i++) {
            byte[] resBlock = new byte[blocks.get(i).length];
            byte[] t = getT(userKey, i);
            for (int j = 0; j < blocks.get(i).length; j++) {
                resBlock[j] = (byte) (blocks.get(i)[j] ^ t[j]);
            }
            result.add(resBlock);
        }
        return result;
    }

    private byte[] padWordToBlockSize(byte[] input) {
        byte[] temp = new byte[BLOCK_SIZE_IN_BYTES];
        System.arraycopy(input, 0, temp, 0, input.length);
        for (int i = input.length; i < BLOCK_SIZE_IN_BYTES; i++) {
            temp[i] = 0;
        }
        return temp;
    }

    private List<Pair<byte[], byte[]>> divideEncryptedDataToLeftAndRight(List<byte[]> encryptedWords) {
        return encryptedWords.stream().map(encryptedWord ->
            new Pair<>(Arrays.copyOfRange(encryptedWord, 0, BLOCK_SIZE_IN_BYTES / 2), Arrays.copyOfRange(encryptedWord, BLOCK_SIZE_IN_BYTES / 2, encryptedWord.length))
        ).collect(Collectors.toList());
    }

    /**
     *
     * @param file
     * @return
     * @throws IOException
     */
    byte[] encrypt(File file, String userKey) throws IOException, NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        if (!file.exists()) {
            throw new IOException("file not exists.");
        }

        List<String> wordsOfFile = getWordsOfFile(file);
        List<byte[]> blockList = mapWordsToBlocks(wordsOfFile);
        List<byte[]> xi = encryptPlainWords(userKey.getBytes(StandardCharsets.UTF_8), blockList);
        List<byte[]> xored = xorEachBlockWithT(xi, userKey);

        return null;
    }

    public static void main(String[] args) {
        File file = new File("src/main/java/me/biabani/encryption/searchable/resource/1.txt");
        System.out.println(file.exists());
    }
}
