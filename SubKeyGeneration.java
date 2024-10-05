public class SubKeyGeneration {
    // 密钥扩展函数
    public static int[][] keyExpansion(int[] K) {
        // 置换表 P10
        int[] P10 = {3, 5, 2, 7, 4, 10, 1, 9, 8, 6};
        // 置换表 P8
        int[] P8 = {6, 3, 7, 4, 8, 5, 10, 9};

        // 用于存储子密钥的数组
        int[][] subKeys = new int[2][8];

        // 对初始密钥进行第一次置换
        int[] K_permuted = permute(K, P10);

        // 进行第一次左移操作操作
        int[] K_Leftshift1 = shiftKey(K_permuted);

        // 生成 k1
        subKeys[0] = permute(K_Leftshift1, P8);

        // 生成 k2
        int[] K_Leftshift2 = shiftKey(K_Leftshift1);
        subKeys[1] = permute(K_Leftshift2, P8);

        return subKeys;
    }

    // 置换函数
    public static int[] permute(int[] input, int[] permutation) {
        int[] output = new int[permutation.length];
        for (int i = 0; i < permutation.length; i++) {
            output[i] = input[permutation[i] - 1]; // 调整为0-based索引
        }
        return output;
    }

    // 数组Leftshift操作
    public static int[] shiftKey(int[] input) {
        int[] output = new int[input.length];
        int middle = input.length/2;
        for(int i = 0; i < middle; i++) {
            output[(i+4)%5] = input[i];
            output[(i+4)%5+middle] = input[i+middle];
        }
        return output;
    }
}
