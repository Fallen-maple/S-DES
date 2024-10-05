public class Operation {
    // 加密方法，根据提供的明文、密钥和加密轮数进行加密操作
    public static int[] encryption(int[] plainTxt, int[] K, int round) {
        // 密钥扩展，生成子密钥
        int[][] key = SubKeyGeneration.keyExpansion(K);
        // 初始置换盒，用于加密过程的初次置换
        int[] IP = {2, 6, 3, 1, 4, 8, 5, 7};
        // 最终置换盒，用于加密过程的最终置换
        int[] FP = {4, 1, 3, 5, 7, 2, 8, 6};
        // 扩展置换盒，用于扩展置换操作
        int[] EPBox = {4, 1, 2, 3, 2, 3, 4, 1};
        // 替换盒，用于替换操作
        int[][][] SBox = {
                {{1, 0, 3, 2}, {3, 2, 1, 0}, {0, 2, 1, 3}, {3, 1, 0, 2}},
                {{0, 1, 2, 3}, {2, 3, 1, 0}, {3, 0, 1, 2}, {2, 1, 0, 3}}
        };
        // 替换置换盒，用于替换操作后的置换
        int[] SPBox = {2, 4, 3, 1};
        int[] temp1 = new int[8];  // 临时数组，用于存储明文分组
        int[] cipherTxt = new int[plainTxt.length];  // 存储密文的数组

        // 对每个8位分组进行加密操作
        for(int n = 0; n < plainTxt.length; n += 8) {
            System.arraycopy(plainTxt, n, temp1, 0, 8);  // 从明文中提取8位分组
            int[] temp2 = permute(temp1, IP);  // 对提取的分组进行初始置换

            // 进行round次的加密轮处理
            for (int j = 0; j < round; j++) {
                int[] LeftTxt = new int[4];  // 左半部分
                int[] RightTxt = new int[4];  // 右半部分
                System.arraycopy(temp2, 0, LeftTxt, 0, 4);
                System.arraycopy(temp2, 4, RightTxt, 0, 4);

                int[] EPTxt = permute(RightTxt, EPBox);  // 对右半部分进行扩展置换
                int[] FTxt = XorTxt(EPTxt, key[j]);  // 与子密钥进行异或操作
                int[] STxt = Substitution(FTxt, SBox);  // 进行替换操作
                int[] SPTxt = permute(STxt, SPBox);  // 进行替换置换操作
                int[] XorLeft = XorTxt(LeftTxt, SPTxt);  // 与左半部分进行异或操作

                // 根据是否是最后一次轮处理，更新temp2数组
                if ((j + 1) != round) {
                    System.arraycopy(RightTxt, 0, temp2, 0, RightTxt.length);
                    System.arraycopy(XorLeft, 0, temp2, RightTxt.length, XorLeft.length);
                } else {
                    System.arraycopy(XorLeft, 0, temp2, 0, XorLeft.length);
                    System.arraycopy(RightTxt, 0, temp2, XorLeft.length, RightTxt.length);
                }
            }

            temp2 = permute(temp2, FP);  // 进行最终置换
            System.arraycopy(temp2, 0, cipherTxt, n, 8);  // 将密文分组存入密文数组
        }
        return cipherTxt;  // 返回密文数组
    }

    // 解密方法，根据提供的密文、密钥和解密轮数进行解密操作
    public static int[] decryption(int[] cipherTxt, int[] K, int round) {
        // 密钥扩展，生成子密钥
        int[][] key = SubKeyGeneration.keyExpansion(K);
        // 初始置换盒，用于解密过程的初次置换
        int[] IP = {2, 6, 3, 1, 4, 8, 5, 7};
        // 最终置换盒，用于解密过程的最终置换
        int[] FP = {4, 1, 3, 5, 7, 2, 8, 6};
        // 扩展置换盒，用于扩展置换操作
        int[] EPBox = {4, 1, 2, 3, 2, 3, 4, 1};
        // 替换盒，用于替换操作
        int[][][] SBox = {
                {{1, 0, 3, 2}, {3, 2, 1, 0}, {0, 2, 1, 3}, {3, 1, 0, 2}},
                {{0, 1, 2, 3}, {2, 3, 1, 0}, {3, 0, 1, 2}, {2, 1, 0, 3}}
        };
        // 替换置换盒，用于替换操作后的置换
        int[] SPBox = {2, 4, 3, 1};
        int[] temp1 = new int[8];  // 临时数组，用于存储密文分组
        int[] plainTxt = new int[cipherTxt.length];  // 存储明文的数组

        // 对每个8位分组进行解密操作
        for (int n = 0; n < cipherTxt.length; n += 8) {
            System.arraycopy(cipherTxt, n, temp1, 0, 8);  // 从密文中提取8位分组
            int[] temp2 = permute(temp1, IP);  // 对提取的分组进行初始置换

            // 进行round次的解密轮处理
            for (int j = 0; j < round; j++) {
                int[] LeftTxt = new int[4];  // 左半部分
                int[] RightTxt = new int[4];  // 右半部分
                System.arraycopy(temp2, 0, LeftTxt, 0, 4);
                System.arraycopy(temp2, 4, RightTxt, 0, 4);

                int[] EPTxt = permute(RightTxt, EPBox);  // 对右半部分进行扩展置换
                int[] FTxt = XorTxt(EPTxt, key[round - 1 - j]);  // 与子密钥进行异或操作
                int[] STxt = Substitution(FTxt, SBox);  // 进行替换操作
                int[] SPTxt = permute(STxt, SPBox);  // 进行替换置换操作
                int[] XorLeft = XorTxt(LeftTxt, SPTxt);  // 与左半部分进行异或操作

                // 根据是否是最后一次轮处理，更新temp2数组
                if ((j + 1) != round) {
                    System.arraycopy(RightTxt, 0, temp2, 0, RightTxt.length);
                    System.arraycopy(XorLeft, 0, temp2, RightTxt.length, XorLeft.length);
                } else {
                    System.arraycopy(XorLeft, 0, temp2, 0, XorLeft.length);
                    System.arraycopy(RightTxt, 0, temp2, XorLeft.length, RightTxt.length);
                }
            }

            temp2 = permute(temp2, FP);  // 进行最终置换
            System.arraycopy(temp2, 0, plainTxt, n, 8);  // 将明文分组存入明文数组
        }
        return plainTxt;  // 返回明文数组
    }

    // 置换函数
    public static int[] permute(int[] input, int[] permutation) {
        int[] output = new int[permutation.length];
        for (int i = 0; i < permutation.length; i++) {
            output[i] = input[permutation[i] - 1];
        }
        return output;
    }

    // 异或函数
    public static int[] XorTxt(int[] input, int[] k) {
        int[] output = new int[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = (int) (input[i] ^ k[i]);
        }
        return output;
    }

    // 替换函数
    public static int[] Substitution(int[] input, int[][][] Sbox) {
        // 输出数组的长度是4
        int[] output = new int[4];

        for (int i = 0; i < 2; i++) {
            // 计算行索引和列索引
            int row = input[i * 4] * 2 + input[i * 4 + 3];
            int col = input[i * 4 + 1] * 2 + input[i * 4 + 2];

            if (row >= Sbox[i].length || col >= Sbox[i][row].length) {
                System.out.println("Exception: Row index out of bounds - Row: " + row + ", Col: " + col);
                throw new ArrayIndexOutOfBoundsException("Sbox index out of bounds at row: " + row + ", col: " + col);
            }

            // 从S盒获取替换值
            int substitutionValue = Sbox[i][row][col];

            // 将替换值的高两位和低两位分别存储到输出数组
            output[i * 2] = (substitutionValue >>> 1) & 0x01; // 高位
            output[i * 2 + 1] = substitutionValue & 0x01; // 低位
        }
        return output;
    }
}
