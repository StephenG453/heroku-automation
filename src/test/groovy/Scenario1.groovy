import groovyx.net.http.*
import groovyx.net.http.*
import spock.lang.Specification
import static groovyx.net.http.ContentType.JSON

class Scenario1 extends Specification {

    //test 404 error NOT FOUND

    def client = new RESTClient('http://credit-test.herokuapp.com/')

    def 'user can create a new line of credit, make one withdrawal, and retrieve info after 30 days'() {
        //user creates brand new line of credit
        when:
        def response = client.post(path : 'api/v1/credit',
                contentType: JSON,
                body: [accountNumber          : '1234',
                       apr         : 35,
                       remainingCreditLimit: 1000,
                       balance: 0,
                       accountCreationDate : '08/01/2020', //might not need this
                       lastModifcationDate : '08/01/2020', //might not need this
                       interestAccrued : 0,
                       totalAmountDue: 0])
        then:
        assert response.status == 201 : 'The new credit was not created. Please try again'

        //retrieve the interest accrued so far, total debt, and remainingCreditLimit on their line of credit
        and:
        response = client.get(path: 'api/v1/credit/1234')

        then:
        assert response.status == 200 : 'could not retrieve current account information'
        assert response['apr'] == 35 : 'The expected APR is incorrect'
        assert response['remainingCreditLimit'] == 1000 : 'The expected credit limit is incorrect'
        assert response['balance'] == 0 : 'The expected balance is incorrect'
        assert response['interestAccrued'] == 0 : 'The expected interest accrued is incorrect'
        assert response['totalAmountDue'] == 0 : 'The expected total amount due is incorrect'

        //withdraw a valid amount from their credit - $500 on day 1
        and:
        response = client.put(path: 'api/v1/credit/1234',
                contentType: JSON,
                body: [accountNumber      : '1234',
                       apr                : 35,
                       remainingCreditLimit        : 500,
                       balance            : 500,
                       accountCreationDate: '08/01/2020',
                       lastModifcationDate : '08/01/2020',
                       interestAccrued: 0,
                       totalAmountDue: 500])
        then:
        assert response.status == 201 : 'update was unsuccessful and new resource was not created'

        //on the 30th the account holder retrieves their account information
        and:
        response = client.get(path: 'api/v1/credit/1234')

        then:
        assert response.status == 200 : 'could not retrieve current account information'
        assert response['apr'] == 35 : 'The expected APR is incorrect'
        assert response['remainingCreditLimit'] == 500 : 'The expected credit limit is incorrect'
        assert response['balance'] == 500 : 'The expected balance is incorrect'
        assert response['interestAccrued'] == 14.38 : 'The expected interest accrued is incorrect'
        assert response['totalAmountDue'] == 514.38 : 'The expected total amount due is incorrect'

        //cleanup to delete everything
    }

    def 'user can create a new line of credit, make one payment, make one withdrawal, and retrieve info after 30 days'() {
        //user creates brand new line of credit
        when:
        def response = client.post(path: 'api/v1/credit',
                contentType: JSON,
                body: [accountNumber          : '1234',
                       apr         : 35,
                       remainingCreditLimit: 1000,
                       balance: 0,
                       accountCreationDate : '08/01/2020',
                       lastModifcationDate : '08/01/2020',
                       interestAccrued : 0,
                       totalAmountDue : 0])
        then:
        assert response.status == 201 : 'the new credit line was not created'

        //retrieve the interest accrued so far, total debt, and remainingCreditLimit on their line of credit
        and:
        response = client.get(path: 'api/v1/credit/1234')

        then:
        assert response.status == 200 : 'could not retrieve current account information'
        assert response['apr'] == 35 : 'The expected APR is incorrect'
        assert response['remainingCreditLimit'] == 1000 : 'The expected credit limit is incorrect'
        assert response['balance'] == 0 : 'The expected balance is incorrect'
        assert response['interestAccrued'] == 0 : 'The expected interest accrued is incorrect'
        assert response['totalAmountDue'] == 0 : 'The expected total amount due is incorrect'

        //withdraw a valid amount from their credit - $500 on day 1
        and:
        response = client.put(path: 'api/v1/credit/1234',
                contentType: JSON,
                body: [accountNumber      : '1234',
                       apr                : 35,
                       remainingCreditLimit        : 500,
                       balance            : 500,
                       accountCreationDate: '08/01/2020',
                       lastModifcationDate : '08/01/2020',
                       interestAccrued: 0,
                       totalAmountDue: 500])
        then:
        assert response.status == 201 : 'update was unsuccessful and new resource was not created'

        //retrieve the interest accrued so far, total debt, and remainingCreditLimit on their line of credit
        and:
        response = client.get(path: 'api/v1/credit/1234')

        then:
        assert response.status == 200 : 'could not retrieve current account information'
        assert response['apr'] == 35 : 'The expected APR is incorrect'
        assert response['remainingCreditLimit'] == 500 : 'The expected credit limit is incorrect'
        assert response['balance'] == 500 : 'The expected balance is incorrect'
        assert response['interestAccrued'] == 0 : 'The expected interest accrued is incorrect'
        assert response['totalAmountDue'] == 500 : 'The expected total amount due is incorrect'

        //user pays back $200 on the 15th of the month
        and:
        response = client.put(path: 'api/v1/credit/1234',
                contentType: JSON,
                body: [accountNumber      : '1234',
                       apr                : 35,
                       remainingCreditLimit        : 700,
                       balance            : 300,
                       accountCreationDate: '08/01/2020',
                       lastModifcationDate : '08/15/2020',
                       interestAccrued: 7.19,
                       totalAmountDue: 500]) //calculate this up to the 15th of the month
        then:
        assert response.status == 201 : 'update was unsuccessful and new resource was not created'

        //retrieve the interest accrued so far, total debt, and remainingCreditLimit on their line of credit
        and:
        response = client.get(path: 'api/v1/credit/1234')

        //since we are still inside the 30 day window, we do not add interest to total amount due
        then:
        assert response.status == 200 : 'could not retrieve current account information'
        assert response['apr'] == 35 : 'The expected APR is incorrect'
        assert response['remainingCreditLimit'] == 700 : 'The expected credit limit is incorrect'
        assert response['balance'] == 300 : 'The expected balance is incorrect'
        assert response['interestAccrued'] == 7.19 : 'The expected interest accrued is incorrect'
        assert response['totalAmountDue'] == 300 : 'The expected total amount due is incorrect'

        //user withdraws $100 on the 25th of the month
        and:
        response = client.put(path: 'api/v1/credit/1234',
                contentType: JSON,
                body: [accountNumber      : '1234',
                       apr                : 35,
                       remainingCreditLimit        : 600,
                       balance            : 400,
                       accountCreationDate: '08/01/2020',
                       lastModifcationDate : '08/25/2020',
                       interestAccrued: 10.07,
                       totalAmountDue: 400])
        then:
        assert response.status == 201 : 'update was unsuccessful and new resource was not created'

        //retrieve the interest accrued so far, total debt, and remainingCreditLimit on their line of credit
        and:
        response = client.get(path: 'api/v1/credit/1234')

        //since we are still inside the 30 day window, we do not add interest to total amount due
        then:
        assert response.status == 200 : 'could not retrieve current account information'
        assert response['apr'] == 35 : 'The expected APR is incorrect'
        assert response['remainingCreditLimit'] == 600 : 'The expected credit limit is incorrect'
        assert response['balance'] == 400 : 'The expected balance is incorrect'
        assert response['interestAccrued'] == 10.07 : 'The expected interest accrued is incorrect'
        assert response['totalAmountDue'] == 400 : 'The expected total amount due is incorrect'

        //on the 30th, retrieve the interest accrued so far, total debt, and remainingCreditLimit on their line of credit
        and:
        response = client.get(path: 'api/v1/credit/1234')

        //30 days has arrived - now interest is added to the total amount due
        then:
        assert response.status == 200 : 'could not retrieve current account information'
        assert response['apr'] == 35 : 'The expected APR is incorrect'
        assert response['remainingCreditLimit'] == 600 : 'The expected credit limit is incorrect'
        assert response['balance'] == 400 : 'The expected balance is incorrect'
        assert response['interestAccrued'] == 11.99 : 'The expected interest accrued is incorrect'
        assert response['totalAmountDue'] == 411.99 : 'The expected total amount due is incorrect'

        //add cleanup

    }

//TODO: edge cases

// add a new line of credit with same id - should return 500 Internal Server Error stating that ID already exists

// add a new line of credit outside the max credit amount allowed - should return 500 error stating the credit amount
//exceeds the limit

// consolidate ^^
// add a new line of credit with APR outside the max allowed - should return 500 error stating the APR exceeds the limit

//add a new line of credit with creditAmount as a negative value - return 500 error stating creditAmount should be a
// positive whole number

// ^^ consolidate
//add a new line of credit with APR as a negative value - return 500 error stating APR should be a positive whole number

    // try to withdraw an amount greater than the current creditAmount - return 500 error stating withdrawal amount is too big
}