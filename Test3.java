import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class Test3 extends JFrame {
    private JTextArea txtPlaintext; // 明文字段文本框
    private JTextArea txtKey; // 密钥文本框
    private JTextArea txtCiphertext; // 输出文本框
    private JButton btnEncrypt; // 加密按钮
    private JButton btnDecrypt; // 解密按钮

    public Test3() {
        super("ASCII Encryption/Decryption Tool");
        initComponents();
    }

    private void initComponents() {
        // 设置布局和边距
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // 创建文本区域和滚动面板
        txtPlaintext = new JTextArea(3, 30);
        txtKey = new JTextArea(3, 30);
        txtCiphertext = new JTextArea(3, 30);
        txtCiphertext.setEditable(false);

        JScrollPane scrollPlaintext = new JScrollPane(txtPlaintext);
        JScrollPane scrollKey = new JScrollPane(txtKey);
        JScrollPane scrollCiphertext = new JScrollPane(txtCiphertext);

        // 创建按钮
        btnEncrypt = new JButton("Encrypt");
        btnDecrypt = new JButton("Decrypt");

        // 创建标签
        JLabel labelPlaintext = new JLabel("Enter inputText (ASCII):", SwingConstants.LEFT);
        JLabel labelKey = new JLabel("Enter Key (ASCII):", SwingConstants.LEFT);
        JLabel labelCiphertext = new JLabel("OutputText (ASCII):", SwingConstants.LEFT);

        // 添加组件到主面板
        gbc.gridy = 0;
        mainPanel.add(labelPlaintext, gbc);

        gbc.gridy = 1;
        mainPanel.add(scrollPlaintext, gbc);

        gbc.gridy = 2;
        mainPanel.add(labelKey, gbc);

        gbc.gridy = 3;
        mainPanel.add(scrollKey, gbc);

        gbc.gridy = 4;
        mainPanel.add(labelCiphertext, gbc);

        gbc.gridy = 5;
        mainPanel.add(scrollCiphertext, gbc);

        // 创建按钮面板并添加按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(btnEncrypt);
        buttonPanel.add(btnDecrypt);

        gbc.gridy = 6;
        mainPanel.add(buttonPanel, gbc);

        // 添加操作
        btnEncrypt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performAction(true);
            }
        });

        btnDecrypt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performAction(false);
            }
        });

        // 设置主框架
        add(mainPanel, BorderLayout.CENTER);
        btnEncrypt.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnDecrypt.setFont(new Font("SansSerif", Font.PLAIN, 14));
        labelPlaintext.setFont(new Font("SansSerif", Font.BOLD, 16));
        labelKey.setFont(new Font("SansSerif", Font.BOLD, 16));
        labelCiphertext.setFont(new Font("SansSerif", Font.BOLD, 16));

        setSize(700, 500); // 设置窗口大小
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void performAction(boolean encrypt) {
        // 获取文本和密钥
        String Text = txtPlaintext.getText();
        String key = txtKey.getText();

        // 验证密钥长度和内容
        if (key.length() != 10 || !key.matches("[01]{10}")) {
            JOptionPane.showMessageDialog(this, "密钥长度必须为10位且只包含0和1！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 转换文本和密钥为二进制数组
        byte[] inputText = Text.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1);
        int[] binaryInputText = new int[inputText.length * 8];
        int destPos = 0;
        for (byte b : inputText) {
            int unsignedByte = b & 0xFF;
            System.arraycopy(toIntArray(unsignedByte), 0, binaryInputText, destPos, 8);
            destPos += 8;
        }

        int[] inputKey = new int[key.length()];
        for (int i = 0; i < key.length(); i++) {
            inputKey[i] = key.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1)[i] - '0';
        }

        // 加密或解密
        int[] resultArray = encrypt ? Operation.encryption(binaryInputText, inputKey, 2)
                : Operation.decryption(binaryInputText, inputKey, 2);

        // 转换结果为字符串并显示
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < resultArray.length; i += 8) {
            int byteValue = 0;
            for (int j = 0; j < 8; j++) {
                byteValue |= (resultArray[i + j] & 0x01) << (7 - j);
            }
            sb.append((char) (byteValue & 0xFF));
        }
        String convertedString = sb.toString();
        txtCiphertext.setText(convertedString);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Test3();
            }
        });
    }

    public static int[] toIntArray(int unsignedByte) {
        int[] bits = new int[8];
        for (int i = 0; i < 8; i++) {
            bits[i] = (unsignedByte & (1 << (7 - i))) != 0 ? 1 : 0;
        }
        return bits;
    }
}
