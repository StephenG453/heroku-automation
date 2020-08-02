import groovyx.net.http.RESTClient
import spock.lang.Specification

import static groovyx.net.http.ContentType.JSON

class E2EBoundaryCases extends Specification {

    //test 404 error NOT FOUND

    def client = new RESTClient('http://credit-test.herokuapp.com/')

   def 'user should not be able to create a duplicate line of credit with the same ID'() {
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

       and:
       response = client.post(path : 'api/v1/credit',
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
       assert response.status == 500 : 'The new account with a duplicate ID was invalidly created.'
   }

    //let's assume the bank has a max credit limit for all customers at 100,000
    def 'user should not be able to create a line of credit outside the max specified by the bank'() {
        //user attempts to create a brand new line of credit outside the bank's credit limit
        def response = client.post(path : 'api/v1/credit',
                contentType: JSON,
                body: [accountNumber          : '1234',
                       apr         : 35,
                       remainingCreditLimit: 100001,
                       balance: 0,
                       accountCreationDate : '08/01/2020', //might not need this
                       lastModifcationDate : '08/01/2020', //might not need this
                       interestAccrued : 0,
                       totalAmountDue: 0])
        then:
        assert response.status == 500 : 'The new account outside the credit limit was invalidly created.'
    }

    //let's assume the bank has a max APR limit for all customers at 60
    def 'user should not be able to create a line of credit outside the max APR specified by the bank'() {
        //user attempts to create a brand new line of credit outside the bank's credit limit
        def response = client.post(path : 'api/v1/credit',
                contentType: JSON,
                body: [accountNumber          : '1234',
                       apr         : 61,
                       remainingCreditLimit: 1000,
                       balance: 0,
                       accountCreationDate : '08/01/2020', //might not need this
                       lastModifcationDate : '08/01/2020', //might not need this
                       interestAccrued : 0,
                       totalAmountDue: 0])
        then:
        assert response.status == 500 : 'The new account outside the APR limit was invalidly created.'
    }

    def 'user should not be able to create a line of credit with a negative credit limit value'() {
        //user attempts to create a brand new line of credit outside the bank's credit limit
        def response = client.post(path : 'api/v1/credit',
                contentType: JSON,
                body: [accountNumber          : '1234',
                       apr         : 35,
                       remainingCreditLimit: -1,
                       balance: 0,
                       accountCreationDate : '08/01/2020', //might not need this
                       lastModifcationDate : '08/01/2020', //might not need this
                       interestAccrued : 0,
                       totalAmountDue: 0])
        then:
        assert response.status == 500 : 'The new account with a negative credit limit was invalidly created.'
    }

    def 'user should not be able to create a line of credit with a negative APR value'() {
        //user attempts to create a brand new line of credit outside the bank's credit limit
        def response = client.post(path : 'api/v1/credit',
                contentType: JSON,
                body: [accountNumber          : '1234',
                       apr         : -35,
                       remainingCreditLimit: 1000,
                       balance: 0,
                       accountCreationDate : '08/01/2020', //might not need this
                       lastModifcationDate : '08/01/2020', //might not need this
                       interestAccrued : 0,
                       totalAmountDue: 0])
        then:
        assert response.status == 500 : 'The new account with a negative APR was invalidly created.'
    }

    def 'user should not be able to withdraw an amount greater than the current credit limit'() {
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

        //attempt to withdraw an invalid amount - 1001 on day 1
        and:
        response = client.put(path: 'api/v1/credit/1234',
                contentType: JSON,
                body: [accountNumber      : '1234',
                       apr                : 35,
                       remainingCreditLimit        : -1,
                       balance            : 1001,
                       accountCreationDate: '08/01/2020',
                       lastModifcationDate : '08/01/2020',
                       interestAccrued: 0,
                       totalAmountDue: 1001])
        then:
        assert response.status == 500 : 'update of withdrawing over the limit was invalidly successful'
    }

//TODO: extra edge cases

    //test 404 error NOT FOUND
    // authorization required access this API? If so test 401/403 error
    // test 400 error with a few invalid request formats

}