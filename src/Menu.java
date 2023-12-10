import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class Menu {
    private static String userKey = "";
    private static JTextArea resultTextArea;

    public static void main(String[] args) {
        final JFrame frame = new JFrame("Menu");

        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");

        JMenu textSubMenu = new JMenu("Text");
        JMenuItem encryptItem = new JMenuItem("Encrypt");
        JMenuItem decryptItem = new JMenuItem("Decrypt");

        textSubMenu.add(encryptItem);
        textSubMenu.add(decryptItem);

        fileMenu.add(textSubMenu);

        JMenuItem settingsItem = new JMenuItem("Settings");

        settingsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSettingsDialog(frame);
            }
        });

        fileMenu.add(settingsItem);

        JMenuItem exitItem = new JMenuItem("Exit");

        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        fileMenu.add(exitItem);

        menuBar.add(fileMenu);

        encryptItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showEncryptionPanel(frame);
            }
        });

        decryptItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDecryptionPanel(frame);
            }
        });

        frame.setJMenuBar(menuBar);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setResizable(true);
    }

    private static void showSettingsDialog(JFrame frame) {
        JTextField keyField = new JTextField(20);

        keyField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetter(c)) {
                    e.consume(); // Ignore non-letter characters
                }
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Enter Encryption/Decryption Key(Current key - " + userKey + "):"));
        panel.add(keyField);

        int result = JOptionPane.showConfirmDialog(frame, panel, "Settings", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String userInput = keyField.getText();
            if (!userInput.isEmpty()) {
                userKey = userInput;
                JOptionPane.showMessageDialog(frame, "Key saved: " + userKey);
            } else {
                JOptionPane.showMessageDialog(frame, "Key cannot be empty.");
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Key was not saved.");
        }
    }


    private static void showEncryptionPanel(JFrame frame) {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JTextArea messageTextArea = new JTextArea(10, 20);

        messageTextArea.addKeyListener(new KeyAdapter() { //only accept letters in message area
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (c == ' ') {
                    // allow space
                }
                else if (!Character.isLetter(c)) {
                    e.consume();
                }
            }
        });

       resultTextArea = new JTextArea(10, 20);
       resultTextArea.setEditable(false);

        JTextField encryptionKeyField = new JTextField(20);
        encryptionKeyField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetter(c)) {
                    e.consume(); // Ignore non-letter characters
                }
            }
        });

        JButton encryptButton = new JButton("Encrypt");

        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get the encryption key from the settings
                String savedKey = userKey;

                // check if the entered key is empty
               /* if (encryptionKeyField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter an encryption key.");
                    return;
                }*/

                // check if the entered key matches the saved key
                if (savedKey.isBlank()) {
                    JOptionPane.showMessageDialog(frame, "Encryption key is empty. Please check your settings.");
                    return;
                } else if (!encryptionKeyField.getText().equals(savedKey)) {
                    JOptionPane.showMessageDialog(frame, "Incorrect encryption key. Please check your settings.");
                    return;
                }


                if (messageTextArea.getText().isBlank()) {
                    JOptionPane.showMessageDialog(frame, "Message cannot be empty.");
                } else {
                    String message = messageTextArea.getText().toUpperCase(); // Convert message to uppercase
                    String key = encryptionKeyField.getText().toLowerCase(); // Convert key to lowercase

                    // convert the encryption key to an array of integers
                    List<Integer> keyList = new ArrayList<>();

                    for (int i = 0; i < key.length(); i++) {
                        char keyChar = key.charAt(i);

                        if (Character.isLetter(keyChar)) {
                            int charValue = Character.toUpperCase(keyChar) - 'A' + 1;
                            int digit = charValue;

                            // Temporary list to store digits in reverse order
                            List<Integer> tempKeyList = new ArrayList<>();

                            // Split multi-digit numbers into separate digits
                            while (digit > 0) {
                                tempKeyList.add(0, digit % 10);  // Prepend each digit to the temporary list
                                digit /= 10;
                            }

                            // Add digits to the main keyList in the correct order
                            keyList.addAll(tempKeyList);
                            // System.out.println(keyList); //print code to check keylist

                            //keyList.add(charValue);
                        } else if (Character.isDigit(keyChar)) { //placeholder code for how to handle numbers in the key
                            int digit = Character.getNumericValue(keyChar);
                            // Split multi-digit numbers into separate digits
                            while (digit > 0) {
                                keyList.add(digit % 10);
                                digit /= 10;
                            }
                        }
                    }

                    // Convert the list to an array
                    int[] keyArray = keyList.stream().mapToInt(Integer::intValue).toArray();


                    // encrypt the message
                    StringBuilder encryptedMessage = new StringBuilder();
                    int keyIndex = 0;

                    for (int i = 0; i < message.length(); i++) {
                        char currentChar = message.charAt(i);
                        if (Character.isLetter(currentChar)) {
                            int charValue = currentChar - 'A' + 1;
                            int keyDigit = keyArray[keyIndex % keyArray.length];

                            // calculate encrypted value
                            int encryptedValue = (charValue + keyDigit - 1) % 26 + 1;

                            char encryptedChar = (char) (encryptedValue + 'A' - 1);
                            encryptedMessage.append(encryptedChar);

                            keyIndex++;
                        } else {
                            // Non-alphabetic characters remain unchanged (placeholder)
                            encryptedMessage.append(currentChar);
                        }
                    }


                    // display the encrypted result in the result area without spaces
                    String encryptedResult = encryptedMessage.toString().replace(" ", "");

                    // display the encrypted result without spaces on the first line
                    resultTextArea.setText(encryptedResult.replace(" ", "") + "\n");
                    resultTextArea.append("\n"); // add a new line

                    // group the result based on the string length of the encryption key
                    int keyLength = key.length();
                    StringBuilder groupedResult = new StringBuilder();
                    int charCount = 0;

                    for (char c : encryptedResult.toCharArray()) {
                        groupedResult.append(c);
                        charCount++;

                        // insert a space after each group of characters equal to the key length
                        if (charCount == keyLength) {
                            groupedResult.append(" ");
                            charCount = 0;
                        }
                    }

                    // if the last group is lacking characters, insert '0' to complete it
                    while (charCount != 0 && charCount < keyLength) {
                        groupedResult.append("0");
                        charCount++;
                    }

                    resultTextArea.append(groupedResult.toString().trim());
                    resultTextArea.append("\n\n"); // add a new line

                    // reverse each group of characters separately
                    String[] groups = groupedResult.toString().trim().split(" ");
                    StringBuilder reversedGroups = new StringBuilder();

                    for (String group : groups) {
                        StringBuilder reversedGroup = new StringBuilder(group).reverse();
                        reversedGroups.append(reversedGroup).append(" ");
                    }

                    resultTextArea.append(reversedGroups.toString().trim());
                    resultTextArea.append("\n\n"); // add a new line

                    // remove spaces between reversed groups
                    String finalResult = reversedGroups.toString().replace(" ", "");
                    resultTextArea.append(finalResult);


                }
            }
        });

        // disable the encrypt button initially
        encryptButton.setEnabled(false);

        // add a document listener to enable the encrypt button when the encryption key is entered
        encryptionKeyField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                encryptButton.setEnabled(true);
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                encryptButton.setEnabled(!encryptionKeyField.getText().isEmpty());
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                // Plain text components do not fire these events
            }
        });


        // Set the layout constraints for resizing and padding
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(10, 20, 0, 5); // Set padding (top, left, bottom, right)

        // Add components to the main panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        gbc.weighty= 0.0;
        mainPanel.add(new JLabel("Message"), gbc);

        gbc.gridy++;
        gbc.gridheight = 2;
        gbc.weighty = 1.0;
        mainPanel.add(new JScrollPane(messageTextArea), gbc);

        gbc.gridx = 0;
        gbc.gridy += 2;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weighty= 0.0;
        mainPanel.add(new JLabel("Decryption Key"), gbc);

        gbc.gridy++;
        mainPanel.add(encryptionKeyField, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(5, 20, 20, 5); // Set padding (top, left, bottom, right)
        mainPanel.add(encryptButton, gbc);

        gbc.insets = new Insets(10, 5, 0, 20); // Set padding (top, left, bottom, right)

        gbc.gridx += 2;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(new JLabel("Result"), gbc);

        gbc.gridy++;
        gbc.weighty= 1.0;
        mainPanel.add(new JScrollPane(resultTextArea), gbc);

        // Set up the window
        frame.getContentPane().removeAll();
        frame.add(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.revalidate();
        frame.repaint();
    }

    private static void showDecryptionPanel(JFrame frame) {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JTextArea messageTextArea = new JTextArea(10, 20);

        messageTextArea.addKeyListener(new KeyAdapter() { //only accept letters in message area
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (c == ' ') {
                    // Ignore non-letter characters
                }
                else if (!Character.isLetter(c)) {
                    e.consume();
                }
            }
        });

        resultTextArea = new JTextArea(10, 20);
        resultTextArea.setEditable(false);
        JTextField decryptionKeyField = new JTextField(20);
        decryptionKeyField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isLetter(c)) {
                    e.consume(); // Ignore non-letter characters
                }
            }
        });



        JButton decryptButton = new JButton("Decrypt");

        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get the encryption key from the settings
                String savedKey = userKey;

                // check if the entered key is empty
               /* if (decryptionKeyField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Please enter an encryption key.");
                    return;
                }*/

                // check if the entered key matches the saved key or is empty
                if (savedKey.isBlank()) {
                    JOptionPane.showMessageDialog(frame, "Decryption key is empty. Please check your settings.");
                    return;
                } else if (!decryptionKeyField.getText().equals(savedKey)) {
                    JOptionPane.showMessageDialog(frame, "Incorrect decryption key. Please check your settings.");
                    return;
                }


                if (messageTextArea.getText().isBlank()) {
                    JOptionPane.showMessageDialog(frame, "Message cannot be empty.");
                } else {
                    String message = messageTextArea.getText().toUpperCase(); // Convert message to uppercase
                    String key = decryptionKeyField.getText().toLowerCase(); // Convert key to lowercase

                    // convert the decryption key to an array of integers
                    List<Integer> keyList = new ArrayList<>();

                    for (int i = 0; i < key.length(); i++) {
                        char keyChar = key.charAt(i);

                        if (Character.isLetter(keyChar)) {
                            int charValue = Character.toUpperCase(keyChar) - 'A' + 1;
                            int digit = charValue;

                            // Temporary list to store digits in reverse order
                            List<Integer> tempKeyList = new ArrayList<>();

                            // Split multi-digit numbers into separate digits
                            while (digit > 0) {
                                tempKeyList.add(0, digit % 10);  // Prepend each digit to the temporary list
                                digit /= 10;
                            }

                            // Add digits to the main keyList in the correct order
                            keyList.addAll(tempKeyList);
                            // System.out.println(keyList); //print code to check keylist

                            //keyList.add(charValue);
                        } else if (Character.isDigit(keyChar)) { //placeholder code for how to handle numbers in the key
                            int digit = Character.getNumericValue(keyChar);
                            // Split multi-digit numbers into separate digits
                            while (digit > 0) {
                                keyList.add(digit % 10);
                                digit /= 10;
                            }
                        }
                    }

                    // Convert the list to an array
                    int[] keyArray = keyList.stream().mapToInt(Integer::intValue).toArray();


                    // group the result based on the string length of the encryption key
                    int keyLength = key.length();
                    StringBuilder groupedResult = new StringBuilder();
                    int charCount = 0;

                    for (char c : message.toCharArray()) {
                        groupedResult.append(c);
                        charCount++;

                        // insert a space after each group of characters equal to the key length
                        if (charCount == keyLength) {
                            groupedResult.append(" ");
                            charCount = 0;
                        }
                    }

                    // reverse each group of characters separately
                    String[] groups = groupedResult.toString().trim().split(" ");
                    StringBuilder reversedGroups = new StringBuilder();

                    for (String group : groups) {
                        StringBuilder reversedGroup = new StringBuilder(group).reverse();
                        reversedGroups.append(reversedGroup).append(" ");
                    }

                    String finalResult = reversedGroups.toString().replace(" ", "");


                    // Decrypt the message using the key
                    StringBuilder decryptedMessage = new StringBuilder();
                    int keyIndex = 0;

                    for (int i = 0; i < finalResult.length(); i++) {
                        char currentChar = finalResult.charAt(i);
                        if (Character.isLetter(currentChar)) {
                            int charValue = currentChar - 'A' + 1;
                            int keyDigit = keyArray[keyIndex % keyArray.length];

                            // calculate decrypted value
                            int decryptedValue = (charValue - keyDigit + 26) % 26;

                            char decryptedChar = (char) (decryptedValue + 'A' - 1);
                            resultTextArea.append(currentChar + " - " + keyDigit + " = " + decryptedChar + "\n");
                            decryptedMessage.append(decryptedChar);

                            keyIndex++;
                        } else {
                            // Non-alphabetic characters remain unchanged (placeholder)
                            decryptedMessage.append(currentChar);
                        }
                    }

                    String decryptedFinal = decryptedMessage.toString().replaceAll("0*$", "");

                    // Display the decrypted result
                    resultTextArea.append("\n\n" + decryptedFinal);


                }
            }
        });

        // disable the encrypt button initially
        decryptButton.setEnabled(false);

        // add a document listener to enable the encrypt button when the encryption key is entered
        decryptionKeyField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                decryptButton.setEnabled(true);
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                decryptButton.setEnabled(!decryptionKeyField.getText().isEmpty());
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                // Plain text components do not fire these events
            }
        });


        // Set the layout constraints for resizing and padding
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(10, 20, 0, 5); // Set padding (top, left, bottom, right)

        // Add components to the main panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        gbc.weighty= 0.0;
        mainPanel.add(new JLabel("Message"), gbc);

        gbc.gridy++;
        gbc.gridheight = 2;
        gbc.weighty = 1.0;
        mainPanel.add(new JScrollPane(messageTextArea), gbc);

        gbc.gridx = 0;
        gbc.gridy += 2;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weighty= 0.0;
        mainPanel.add(new JLabel("Decryption Key"), gbc);

        gbc.gridy++;
        mainPanel.add(decryptionKeyField, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(5, 20, 20, 5); // Set padding (top, left, bottom, right)
        mainPanel.add(decryptButton, gbc);

        gbc.insets = new Insets(10, 5, 0, 20); // Set padding (top, left, bottom, right)

        gbc.gridx += 2;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(new JLabel("Result"), gbc);

        gbc.gridy++;
        gbc.weighty= 1.0;
        mainPanel.add(new JScrollPane(resultTextArea), gbc);

        // Set up the window
        frame.getContentPane().removeAll();
        frame.add(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.revalidate();
        frame.repaint();

    }



}
