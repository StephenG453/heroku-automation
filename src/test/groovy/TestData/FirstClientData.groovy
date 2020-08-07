package TestData

// This can be scaled to handle different clients with minor differences in functionality by extending an
//Abstract class or Interface
class FirstClientData {

    private final String accountNumber = "1234"
    private final int APR = 35
    private int remainingCreditLimit = 1000
    private int balance = 0

    String getAccountNumber() {
        return accountNumber
    }

    int getAPR() {
        return APR
    }

    // not using these methods in the definitions but it's the same principle as getAPR()
    int getRemainingCreditLimit() {
        return remainingCreditLimit
    }

    void setRemainingCreditLimit(int remainingCreditLimit) {
        this.remainingCreditLimit = remainingCreditLimit
    }

    int getBalance() {
        return balance
    }

    void setBalance(int balance) {
        this.balance = balance
    }

    /*
    import the methods in the source code that calculates the modification date, balance, interestAccrued,
    and totalAmountDue
    */
}