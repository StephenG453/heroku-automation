package TestData

class FirstClientData { // to really scale this class can extends an Abstract class or Interface

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