package me.biabani.se.raw_implementation.boneh;

import me.biabani.se.raw_implementation.common.ArrayUtil;
import me.biabani.se.raw_implementation.common.EncryptionUtil;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class Index {

    public static void aliceKeyGen() throws IOException {
        KeyPair keyPair = EncryptionUtil.rsaKeyGenerator();
        Files.writeString(Paths.get("files/client/private_key_alice.key"), Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
        Files.writeString(Paths.get("files/client/public_key_alice.pem"), Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
    }

    public static void keywordKeyGen(File keywordsFile) throws IOException {
        List<String> keywordList = Files.readAllLines(keywordsFile.toPath());
        for (int i = 1; i <= keywordList.size(); i++) {
            KeyPair keyPair = EncryptionUtil.rsaKeyGenerator();
            Files.writeString(Paths.get(String.format("files/client/private_key_%d.key", i)), Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded()));
            Files.writeString(Paths.get(String.format("files/client/public_key_%d.pem", i)), Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded()));
        }
    }

    public static void encrypt(File message, List<File> keywordFiles) throws IOException, InvalidKeyException {
        List<byte[]> publicKeyByteArrayList = new ArrayList<>();
        for (File publicKeyFile : keywordFiles) {
            publicKeyByteArrayList.add(Base64.getDecoder().decode(Files.readString(publicKeyFile.toPath())));
        }

        // 'M' in article
        byte[] mByteArray = EncryptionUtil.aesGenerateRandomIv();
        byte[] alicePublicKeyByteArray = Base64.getDecoder().decode(Files.readString(Paths.get("files/client/public_key_alice.pem")));
        byte[] ivEncrypted = EncryptionUtil.rsaEncrypt(alicePublicKeyByteArray, mByteArray);
        byte[] mForEncryption = new byte[128];
        System.arraycopy(ivEncrypted, 0, mForEncryption, 0, 128);

        List<byte[]> mEncryptedList = new ArrayList<>();
        for (byte[] publicKeyByteArray : publicKeyByteArrayList) {
            mEncryptedList.add(EncryptionUtil.rsaEncrypt(publicKeyByteArray, mForEncryption));
        }

        String exportFileName = "files/server/" + message.getName() + ".bin";
        File exportFile = new File(exportFileName);
        if (exportFile.exists()) {
            exportFile.delete();
        }
        exportFile.createNewFile();
        Files.write(Paths.get(exportFileName), new byte[]{(byte) mEncryptedList.size()}, StandardOpenOption.APPEND);

        for (byte[] mEncrypted : mEncryptedList) {
            Files.write(Paths.get(exportFileName), mEncrypted, StandardOpenOption.APPEND);
        }


        Files.write(Paths.get(exportFileName), ivEncrypted, StandardOpenOption.APPEND);

        byte[] aesKey = new byte[16];
        System.arraycopy(alicePublicKeyByteArray, 0, aesKey, 0, 16);

        Files.write(Paths.get(exportFileName),
                EncryptionUtil.aesEncryptCBC(aesKey, mByteArray, Files.readAllBytes(message.toPath())),
                StandardOpenOption.APPEND);
    }

    public static void decrypt(File encrypted) throws IOException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        byte[] encryptedFileByteArray = Files.readAllBytes(encrypted.toPath());
        int numberOfKeywords = encryptedFileByteArray[0];
        byte[] ivEncrypted = new byte[256];
        System.arraycopy(encryptedFileByteArray, 1 + numberOfKeywords * 256, ivEncrypted, 0, 256);

        byte[] alicePrivateKeyByteArray = Base64.getDecoder().decode(Files.readString(Paths.get("files/client/private_key_alice.key")));
        byte[] ivDecrypted = EncryptionUtil.rsaDecrypt(alicePrivateKeyByteArray, ivEncrypted);

        byte[] alicePublicKeyByteArray = Base64.getDecoder().decode(Files.readString(Paths.get("files/client/public_key_alice.pem")));
        byte[] aesKey = new byte[16];
        System.arraycopy(alicePublicKeyByteArray, 0, aesKey, 0, 16);

        Files.writeString(Paths.get("files/client/" + encrypted.getName() + ".decrypted"), EncryptionUtil.aesDecrypt(ivDecrypted, aesKey, Arrays.copyOfRange(encryptedFileByteArray, 1 + (numberOfKeywords + 1) * 256, encryptedFileByteArray.length)));
    }

    public static List<String> test(File privateKeyFile) throws IOException {

        byte[] keywordPrivateKey = Base64.getDecoder().decode(Files.readString(privateKeyFile.toPath()));

        File rootFolder = new File("files/server/");

        return Arrays.stream(rootFolder.listFiles()).filter(encryptedFile -> {
            try {
                byte[] encryptedFileByteArray = Files.readAllBytes(encryptedFile.toPath());
                int numberOfKeywords = encryptedFileByteArray[0];
                for (int i = 0; i < numberOfKeywords; i++) {
                    byte[] keywordEncrypted = new byte[256];
                    System.arraycopy(encryptedFileByteArray, 1 + i * 256, keywordEncrypted, 0, 256);
                    byte[] mDecrypted = null;
                    try {
                        mDecrypted = EncryptionUtil.rsaDecrypt(keywordPrivateKey, keywordEncrypted);
                    } catch (Exception ignored) {
                        continue;
                    }
                    byte[] m = new byte[128];
                    System.arraycopy(encryptedFileByteArray, 1 + numberOfKeywords * 256, m, 0, 128);
                    if (ArrayUtil.byteArrayEqual(m, mDecrypted, 128)) {
                        return true;
                    }
                }
                return false;
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).map(file -> file.getAbsolutePath()).collect(Collectors.toList());
    }

    public static void main(String[] args) {
        try {
//            aliceKeyGen();
//            keywordKeyGen(new File("files/client/keywords.txt"));
//            encrypt(new File("files/client/1.txt"),
//                    List.of(new File("files/client/public_key_1.pem"),
//                            new File("files/client/public_key_2.pem"))
//            );
//            decrypt(new File("files/server/1.txt.bin"));
            List<String> test = test(new File("files/client/private_key_3.key"));
            System.out.println(test.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
