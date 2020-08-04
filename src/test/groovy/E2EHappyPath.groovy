import groovyx.net.http.*
import groovyx.net.http.*
import spock.lang.Specification
import static groovyx.net.http.ContentType.JSON

class E2EHappyPath extends Specification {

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
        checkForCorrectDataInResponse(response,200, 35, 1000, 0,
                0, 0)

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
        checkForCorrectDataInResponse(response,200, 35, 500, 500,
                14.38, 514.38)

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
        checkForCorrectDataInResponse(response,200, 35, 1000, 0,
                0, 0)

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
        checkForCorrectDataInResponse(response,200, 35, 500, 500,
                0, 500)

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
        checkForCorrectDataInResponse(response, 200, 35, 700, 300,
                7.19, 300)

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
        checkForCorrectDataInResponse(response, 200, 35, 600, 400,
                10.07, 400)

        //on the 30th, retrieve the interest accrued so far, total debt, and remainingCreditLimit on their line of credit
        and:
        response = client.get(path: 'api/v1/credit/1234')

        //30 days has arrived - now interest is added to the total amount due
        then:
        checkForCorrectDataInResponse(response, 200, 35, 600, 400,
                11.99, 411.99)

        //add cleanup

    }

    void checkForCorrectDataInResponse(response, responseStatus, apr, remainingCreditLimit,
                                       balance, interestAccrued, totalAmountDue) {
        assert response.status == responseStatus : 'could not retrieve current account information'
        assert response['apr'] == apr : 'The expected APR is incorrect'
        assert response['remainingCreditLimit'] == remainingCreditLimit : 'The expected credit limit is incorrect'
        assert response['balance'] == balance : 'The expected balance is incorrect'
        assert response['interestAccrued'] == interestAccrued : 'The expected interest accrued is incorrect'
        assert response['totalAmountDue'] == totalAmountDue : 'The expected total amount due is incorrect'
    }
}