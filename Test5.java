import java.io.UnsupportedEncodingException;
import java.util.Scanner;

public class Test5 {
    public static void main(String[] args) throws UnsupportedEncodingException {
        Scanner scanner = new Scanner(System.in);

        // 获取明文分组和密文分组
        System.out.println("请输入明文分组（8位二进制数）:");
        String plainText = scanner.nextLine();

        int[] inputText = new int[plainText.length()]; // 将明文转换为数字数组
        for (int i = 0; i < plainText.length(); i++) {
            inputText[i] = plainText.getBytes("ASCII")[i] - '0';
        }

        System.out.println("请输入密文分组（8位二进制数）:");
        String cipherText = scanner.nextLine();

        int[] ciphertext = new int[cipherText.length()]; // 将明文转换为数字数组
        for (int i = 0; i < cipherText.length(); i++) {
            ciphertext[i] = cipherText.getBytes("ASCII")[i] - '0';
        }

        int round = 2;

        int numberOfPairs = 1;

        long startTime = System.currentTimeMillis();

        // 初始化密钥计数数组，有1024种可能的密钥（2^10）
        int[] keyCounts = new int[1 << 10];

        // 遍历所有可能的密钥
        for (int i = 0; i < (1 << 10); i++) {
            int[] key = new int[10]; // 创建一个长度为10的密钥数组
            // 将整数i转换为10位的二进制密钥
            for (int j = 0; j < 10; j++) {
                key[j] = (i >> (9 - j)) & 1;
            }

            int correctDecryptions = 0; // 用于计数正确解密的次数
            // 对每一对明密文进行测试
            for (int pair = 0; pair < numberOfPairs; pair++) {
                int[] decryptedTextFlat = Operation.decryption(ciphertext, key, round);
                // 如果解密后的文本与已知明文相同，则增加计数
                if (arraysAreEqual(inputText, decryptedTextFlat)) {
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
                // 打印找到的正确密钥
                System.out.println("找到正确的密钥: " + bitsToInt(intToBits(bestKey, 10)));
            }
        }
        // 打印找到的正确密钥数量
        System.out.println("找到正确的密钥数量为: " + keyNum);

        long endTime = System.currentTimeMillis();
        // 打印破解花费的时间
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
