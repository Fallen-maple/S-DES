import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class Test1 extends JFrame {
    private JTextArea txtPlaintext; // 明文字段文本框
    private JTextArea txtKey; // 密钥文本框
    private JTextArea txtCiphertext; // 密文文本框
    private JRadioButton btnEncrypt; // 加密按钮
    private JRadioButton btnDecrypt; // 解密按钮

    // 将无符号字节转换为位数组
    public static int[] toIntArray(int unsignedByte) {
        int[] bits = new int[8];
        for (int i = 0; i < 8; i++) {
            bits[i] = (unsignedByte & (1 << (7 - i))) != 0 ? 1 : 0;
        }
        return bits;
    }

    // 将位数组转换为字符串
    public static String toString(int[] inputTxt) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < inputTxt.length; i += 8) {
            int byteValue = 0;
            for (int j = 0; j < 8; j++) {
                byteValue |= (inputTxt[i + j] & 0x01) << (7 - j);
            }
            sb.append((char) (byteValue & 0xFF));
        }
        return sb.toString();
    }

    public Test1() {
        setTitle("ASCII加密/解密工具"); // 设置窗口标题
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 设置默认关闭操作
        setLayout(new GridBagLayout()); // 设置布局管理器

        GridBagConstraints gbc = new GridBagConstraints(); // 设置布局约束
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // 创建明文字段面板
        JPanel panelPlaintext = new JPanel(new BorderLayout());
        panelPlaintext.add(new JLabel("输入字串:"), BorderLayout.WEST);
        txtPlaintext = new JTextArea(1, 20);
        panelPlaintext.add(new JScrollPane(txtPlaintext), BorderLayout.EAST);
        add(panelPlaintext, gbc);

        // 创建密钥面板
        JPanel panelKey = new JPanel(new BorderLayout());
        panelKey.add(new JLabel("输入密钥:"), BorderLayout.WEST);
        txtKey = new JTextArea(1, 20);
        panelKey.add(new JScrollPane(txtKey), BorderLayout.CENTER);
        add(panelKey, gbc);

        // 创建密文面板
        JPanel panelCiphertext = new JPanel(new BorderLayout());
        panelCiphertext.add(new JLabel("输出字串:"), BorderLayout.WEST);
        txtCiphertext = new JTextArea(1, 20);
        txtCiphertext.setEditable(false);
        panelCiphertext.add(new JScrollPane(txtCiphertext), BorderLayout.CENTER);
        add(panelCiphertext, gbc);

        ButtonGroup group = new ButtonGroup(); // 按钮组，用于单选按钮
        btnEncrypt = new JRadioButton("加密", true);
        btnDecrypt = new JRadioButton("解密", false);
        group.add(btnEncrypt);
        group.add(btnDecrypt);

        // 创建按钮面板
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelButtons.add(btnEncrypt);
        panelButtons.add(btnDecrypt);
        gbc.insets = new Insets(5, 20, 10, 20);
        add(panelButtons, gbc);

        // 创建执行按钮
        JButton btnExecute = new JButton("执行");
        btnExecute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performAction();
            }
        });
        gbc.insets = new Insets(10, 10, 10, 10);
        add(btnExecute, gbc);

        setSize(600, 300); // 设置窗口大小
        setLocationRelativeTo(null); // 居中显示
        setVisible(true); // 显示窗口
    }

    private void performAction() {
        // 获取文本框内容
        String plaintext = txtPlaintext.getText().trim();
        String key = txtKey.getText().trim();

        // 检查明文长度和内容
        if (plaintext.length() != 8 || !plaintext.matches("[01]{8}")) {
            JOptionPane.showMessageDialog(this, "明文长度必须为8位且只包含0和1！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 验证密钥长度和内容
        if (key.length() != 10 || !key.matches("[01]{10}")) {
            JOptionPane.showMessageDialog(this, "密钥长度必须为10位且只包含0和1！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int[] inputText = new int[plaintext.length()]; // 将明文转换为数字数组
            for (int i = 0; i < plaintext.length(); i++) {
                inputText[i] = plaintext.getBytes("ASCII")[i] - '0';
            }

            int[] inputKey = new int[key.length()]; // 将密钥转换为数字数组
            for (int i = 0; i < key.length(); i++) {
                inputKey[i] = key.getBytes("ASCII")[i] - '0';
            }

            int[] outputText = new int[inputText.length]; // 输出文本数组
            if (btnEncrypt.isSelected()) {
                outputText = Operation.encryption(inputText, inputKey, 2);
            } else if (btnDecrypt.isSelected()) {
                outputText = Operation.decryption(inputText, inputKey, 2);
            }

            // 使用StringBuilder来构建输出字符串
            StringBuilder outputStringBuilder = new StringBuilder();
            for (int i = 0; i < outputText.length; i++) {
                outputStringBuilder.append(outputText[i]);
            }
            txtCiphertext.setText(outputStringBuilder.toString());
        } catch (Exception e) {
            txtCiphertext.setText("错误: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Test1::new);
    }
}
