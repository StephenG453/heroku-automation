package ClientData;

public class FirstClientData { // to really scale this class can extends an Abstract class or Interface

    private final String accountNumber = "1234";
    private final int APR = 35;
    private int remainingCreditLimit = 1000;
    private int balance = 0;

    public String getAccountNumber() {
        return accountNumber;
    }

    public int getAPR() {
        return APR;
    }

    public int getRemainingCreditLimit() {
        return remainingCreditLimit;
    }

    public void setRemainingCreditLimit(int remainingCreditLimit) {
        this.remainingCreditLimit = remainingCreditLimit;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

     /*
     import the methods in the source code that calculates the modification date, balance, interestAccrued,
     and totalAmountDue
     */
}
