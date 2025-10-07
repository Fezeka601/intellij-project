
package org.example;

import javax.swing.*;
        import java.awt.*;
        import java.awt.event.*;
        import java.util.HashMap;

public class ATMSystem extends JFrame implements ActionListener {
    private JTextField textField;
    private JLabel promptLabel;
    private JPanel numberPanel;
    private JButton[] numberButtons = new JButton[10];
    private JButton enterButton, clearButton, cancelButton;
    private JButton depositButton, balanceButton, logoutButton, withdrawButton;

    private String currentState = "ACCOUNT";
    private String inputBuffer = "";
    private int currentAccountNumber = -1;

    private double amount;

    // Bank account class
    class BankAccount {
        String holderName;
        String pin;
        double balance;
        double withdraw;

        public static double Daily_limit = 3000;

        public BankAccount(String holderName, String pin, double balance) {
            this.holderName = holderName;
            this.pin = pin;
            this.balance = balance;
            this.withdraw = 0.0;
        }

        public boolean Withdrawal(double amount) {
            return (amount <= balance) && ((withdraw + amount) <= Daily_limit);
        }

        public void Withdraw(double amount) {
            if (Withdrawal(ATMSystem.this.amount)) {
                balance -= ATMSystem.this.amount;
                withdraw += ATMSystem.this.amount;
            }
        }

        public void resetDaily_limit() {
            withdraw = 0.0;
        }
    }

    // Multiple accounts using a HashMap
    private HashMap<Integer, BankAccount> accounts = new HashMap<>();

    public ATMSystem() {

        accounts.put(2761, new BankAccount("fezeka", "1234", 1200.0));
        accounts.put(2222, new BankAccount("Nhlakanipho", "5678", 800.0));
        accounts.put(3143, new BankAccount("esihle", "9999", 1500.0));


        setTitle("ATM System - Multi User");
        setSize(650, 650);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.DARK_GRAY);

        promptLabel = new JLabel("Enter Account Number:");
        promptLabel.setFont(new Font("Arial", Font.BOLD, 16));
        promptLabel.setBounds(100, 30, 400, 30);

        textField = new JTextField();
        textField.setBounds(100, 70, 400, 50);
        textField.setEditable(false);
        textField.setFont(new Font("Arial", Font.BOLD, 24));
        textField.setBackground(Color.LIGHT_GRAY);

        numberPanel = new JPanel();
        numberPanel.setLayout(new GridLayout(4, 3, 10, 10));
        numberPanel.setBounds(140, 150, 250, 200);
        numberPanel.setBackground(Color.GRAY);

        for (int i = 1; i <= 9; i++) {
            numberButtons[i] = createButton(String.valueOf(i));
            numberPanel.add(numberButtons[i]);
        }

        numberButtons[0] = createButton("0");
        numberPanel.add(new JLabel());
        numberPanel.add(numberButtons[0]);
        numberPanel.add(new JLabel());

        enterButton = createButton("ENTER");
        clearButton = createButton("CLEAR");
        cancelButton = createButton("CANCEL");
        depositButton = createButton("DEPOSIT");
        balanceButton = createButton("BALANCE");
        withdrawButton = createButton("WITHDRAW");
        logoutButton = createButton("LOGOUT");

        enterButton.setBounds(410, 180, 120, 40);
        clearButton.setBounds(410, 230, 120, 40);
        cancelButton.setBounds(410, 280, 120, 40);
        depositButton.setBounds(100, 380, 150, 40);
        balanceButton.setBounds(270, 380, 150, 40);
        withdrawButton.setBounds(270,440, 150,40 );
        logoutButton.setBounds(100, 440, 150, 40);

        add(promptLabel);
        add(textField);
        add(numberPanel);
        add(enterButton);
        add(clearButton);
        add(cancelButton);
        add(depositButton);
        add(balanceButton);
        add(withdrawButton);
        add(logoutButton);

        disableMenuButtons();

        setVisible(true);

    }


        private JButton createButton(String text) {
            JButton btn = new JButton(text);
            btn.addActionListener(this);
            btn.setFocusable(false);
            return btn;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();

            // Number input
            for (int i = 0; i <= 9; i++) {
                if (source == numberButtons[i]) {
                    inputBuffer += i;
                    textField.setText(inputBuffer);
                    return;
                }
            }

            // CLEAR button
            if (source == clearButton) {
                if (!inputBuffer.isEmpty()) {
                    inputBuffer = inputBuffer.substring(0, inputBuffer.length() - 1);
                    textField.setText(inputBuffer);
                }
                return;
            }

            // CANCEL button
            if (source == cancelButton) {
                resetSession("Session canceled. Enter Account Number:");
                return;
            }

            // ENTER button logic
            if (source == enterButton) {
                handleEnter();
                return;
            }

            // DEPOSIT
            if (source == depositButton && currentState.equals("MENU")) {
                currentState = "DEPOSIT";
                inputBuffer = "";
                promptLabel.setText("Enter amount to deposit:");
                textField.setText("");
                return;
            }

            // BALANCE
            if (source == balanceButton && currentState.equals("MENU")) {
                BankAccount acc = accounts.get(currentAccountNumber);
                promptLabel.setText(acc.holderName + "'s Balance: R" + acc.balance);
                textField.setText("");
                return;
            }
            // WITHDRAW
            if (source == withdrawButton && currentState.equals("MENU")){
                currentState = "WITHDRAW";
            inputBuffer = "";
            promptLabel.setText(" Enter amount to withdraw");
            textField.setText("");
            return;
        }
            // LOGOUT
            if (source == logoutButton) {
                resetSession("Logged out. Enter Account Number:");
            }
        }

        private void handleEnter() {
            switch (currentState) {
                case "ACCOUNT":
                    try {
                        currentAccountNumber = Integer.parseInt(inputBuffer);
                        if (accounts.containsKey(currentAccountNumber)) {
                            currentState = "PIN";
                            promptLabel.setText("Enter PIN for Account: " + currentAccountNumber);
                            inputBuffer = "";
                            textField.setText("");
                        } else {
                            promptLabel.setText("Account not found. Try again:");
                            inputBuffer = "";
                            textField.setText("");
                        }
                    } catch (NumberFormatException ex) {
                        promptLabel.setText("Invalid account number.");
                    }
                    break;

                case "PIN":
                    BankAccount account = accounts.get(currentAccountNumber);
                    if (account.pin.equals(inputBuffer)) {
                        currentState = "MENU";
                        enableMenuButtons();
                        promptLabel.setText("Welcome " + account.holderName + ". Choose operation:");
                        textField.setText("");
                    } else {
                        promptLabel.setText("Incorrect PIN. Try again:");
                    }
                    inputBuffer = "";
                    break;

                case "DEPOSIT":
                    try {
                        double amount = Double.parseDouble(inputBuffer);
                        accounts.get(currentAccountNumber).balance += amount;
                        promptLabel.setText("Deposited R" + amount + " successfully.");
                        currentState = "MENU";
                        inputBuffer = "";
                        textField.setText("");
                    } catch (NumberFormatException ex) {
                        promptLabel.setText("Invalid amount. Try again:");
                    }
                    break;

                case"WITHDRAW":
                    try{
                        double Amount = Double.parseDouble(inputBuffer);
                        BankAccount acc = accounts.get(currentAccountNumber);

                        if(acc.Withdrawal(Amount)) {
                            acc.Withdraw(Amount);
                            promptLabel.setText("Withdrawn : R" + Amount);
                        }else if (Amount > acc.balance) {
                            promptLabel.setText("Insufficient funds");
                        } else {
                            promptLabel.setText("Exceeds daily limit");
                        }

                        } catch (NumberFormatException e) {
                        promptLabel.setText("Invalid amount");
                    }
                    inputBuffer = "";
                    textField.setText("");
                    currentState = "MENU";
                default:
                    break;

            }
        }

        private void resetSession(String message) {
            currentState = "ACCOUNT";
            inputBuffer = "";
            currentAccountNumber = -1;
            promptLabel.setText(message);
            textField.setText("");
            disableMenuButtons();
        }

        private void enableMenuButtons() {
            depositButton.setEnabled(true);
            balanceButton.setEnabled(true);
            withdrawButton.setEnabled(true);
            logoutButton.setEnabled(true);
        }

        private void disableMenuButtons() {
            depositButton.setEnabled(false);
            balanceButton.setEnabled(false);
            withdrawButton.setEnabled(false);
            logoutButton.setEnabled(false);
        }


    }

