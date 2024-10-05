import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class Test4 {
    public static void main(String[] args) throws UnsupportedEncodingException {
        Scanner scanner = new Scanner(System.in);

        // 获取明文分组数量
        System.out.println("请输入明文分组的数量:");
        int numberOfPairs = scanner.nextInt();
        scanner.nextLine(); // 消耗换行符

        int[][] knownPlaintext = new int[numberOfPairs][];
        int[][] ciphertext = new int[numberOfPairs][];

        for (int pair = 0; pair < numberOfPairs; pair++) {
            System.out.println("请输入第 " + (pair + 1) + " 组8位二进制明文:");
            String plainText = scanner.nextLine();
            knownPlaintext[pair] = new int[plainText.length()];
            for (int i = 0; i < plainText.length(); i++) {
                knownPlaintext[pair][i] = plainText.getBytes("ASCII")[i] - '0';
            }

            System.out.println("请输入第 " + (pair + 1) + " 组8位二进制密文:");
            String cipherText = scanner.nextLine();
            ciphertext[pair] = new int[cipherText.length()];
            for (int i = 0; i < cipherText.length(); i++) {
                ciphertext[pair][i] = cipherText.getBytes("ASCII")[i] - '0';
            }
        }

        int round = 2; // 加密轮数

        long startTime = System.currentTimeMillis();

        // 初始化密钥计数数组，有1024种可能的密钥（2^10）
        int[] keyCounts = new int[1 << 10];

        for (int i = 0; i < (1 << 10); i++) { // 2^10 = 1024种可能的密钥
            int[] key = new int[10];
            for (int j = 0; j < 10; j++) {
                key[j] = (i >> (9 - j)) & 1;
            }

            int correctDecryptions = 0;

            // 对每对明密文进行测试
            for (int pair = 0; pair < numberOfPairs; pair++) {
                int[] decryptedTextFlat = Operation.decryption(ciphertext[pair], key, round);
                if (arraysAreEqual(knownPlaintext[pair], decryptedTextFlat)) {
                    correctDecryptions++;
                }
            }

            // 如果所有测试都正确，则增加密钥计数
            if (correctDecryptions == numberOfPairs) {
                keyCounts[i]++;
            }
        }

        // 查找所有正确的密钥
        int bestKey = -1;
        int keyNum = 0;
        for (int i = 0; i < keyCounts.length; i++) {
            if (keyCounts[i] != 0) {
                bestKey = i;
                keyNum++;
                System.out.println("找到正确的密钥: " + bitsToInt(intToBits(bestKey, 10)));
            }
        }
        System.out.println("找到正确的密钥数量为: " + keyNum);

        long endTime = System.currentTimeMillis();
        System.out.println("破解花费时间：" + (endTime - startTime) + "毫秒");

        scanner.close();
    }

    private static boolean arraysAreEqual(int[] a, int[] b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) return false;
        }
        return true;
    }

    private static int[] intToBits(int num, int length) {
        int[] bits = new int[length];
        for (int i = 0; i < length; i++) {
            bits[length - 1 - i] = (num >> i) & 1;
        }
        return bits;
    }

    private static String bitsToInt(int[] bits) {
        StringBuilder sb = new StringBuilder();
        for (int bit : bits) {
            sb.append(bit);
        }
        return sb.toString();
    }
}