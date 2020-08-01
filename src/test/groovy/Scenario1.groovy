import groovyx.net.http.*
import groovyx.net.http.*
import spock.lang.Specification
import static groovyx.net.http.ContentType.JSON

class Scenario1 extends Specification {

    //test 404 error NOT FOUND

    def client = new RESTClient('http://credit-test.herokuapp.com/')

    def 'user can create a new line of credit, make one payment, and retrieve info after 30 days'() {
        when:
        def response = client.post(path: 'api/v1/credit',
                contentType: JSON,
                body: [accountNumber          : '1234',
                       apr         : 35,
                       creditLimit: 1000,
                       debit: 0,
                       accountCreationDate : '08/01/2020',
                       lastModifcationDate : '08/01/2020',
                       interestAccrued : 0])
        then:
        assert response.status == 201 : 'the new credit was not created'

        //retrieve the interest accrued so far, total debt, and creditLimit on their line of credit
        and:
        response = client.get(path: 'api/v1/credit/1234')

        then:
        assert response.status == 200 : 'could not retrieve current account information'

        //withdraw a valid amount from their credit
        //in this case it is $500 on the first day
        and:
        response = client.put(path: 'api/v1/credit/1234',
                contentType: JSON,
                body: [accountNumber      : '1234',
                       apr                : 35,
                       creditLimit        : 1000,
                       debt               : 500,
                       accountCreationDate: '08/01/2020',
                       lastModifcationDate : '08/01/2020',
                       interestAccrued: 0])
        then:
        assert response.status == 201 : 'update was unsuccessful and new resource was not created'

        //on the 30th the account holder retrieves their account information
        //interest accrued is $14.38
        //debt is $514.38
        and:
        response = client.get(path: 'api/v1/credit/1234')

        then:
        assert response.status == 200 : 'could not retrieve current account information'
    }
    // get should return interest accrued so far and total debt

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