import groovyx.net.http.RESTClient
import spock.lang.Specification

import static groovyx.net.http.ContentType.JSON

class E2EBoundaryCases extends Specification {

    FirstClientData firstClientData
    def client

    def setUp() {
        client = new RESTClient('http://credit-test.herokuapp.com/')
        firstClientData = new FirstClientData()
    }

   def 'user should not be able to create a duplicate line of credit with the same ID'() {
       when:
       def response = client.post(path : 'api/v1/credit',
               contentType: JSON,
               body: [accountNumber         : firstClientData.getAccountNumber(),
                      apr                   : firstClientData.getAPR(),
                      remainingCreditLimit  : 1000,
                      balance               : 0,
                      lastModifcationDate   : '08/01/2020',
                      interestAccrued       : 0,
                      totalAmountDue        : 0])
       then:
       assert response.status == 201 : 'The new credit was not created. Please try again'

       and:
       response = client.post(path : 'api/v1/credit',
               contentType: JSON,
               body: [accountNumber         : firstClientData.getAccountNumber(),
                      apr                   : firstClientData.getAPR(),
                      remainingCreditLimit  : 1000,
                      balance               : 0,
                      lastModifcationDate   : '08/01/2020',
                      interestAccrued       : 0,
                      totalAmountDue        : 0])
       then:
       assert response.status == 500 : 'The new account with a duplicate ID was invalidly created.'
   }

    //let's assume the bank has a max credit limit for all customers at 100,000
    def 'user should not be able to create a line of credit outside the max specified by the bank'() {
        def response = client.post(path : 'api/v1/credit',
                contentType: JSON,
                body: [accountNumber        : firstClientData.getAccountNumber(),
                       apr                  : firstClientData.getAPR(),
                       remainingCreditLimit : 100001,
                       balance              : 0,
                       lastModifcationDate  : '08/01/2020',
                       interestAccrued      : 0,
                       totalAmountDue       : 0])
        then:
        assert response.status == 500 : 'The new account outside the credit limit was invalidly created.'
    }

    //let's assume the bank has a max APR limit for all customers at 60
    def 'user should not be able to create a line of credit outside the max APR specified by the bank'() {
        def response = client.post(path : 'api/v1/credit',
                contentType: JSON,
                body: [accountNumber          : firstClientData.getAccountNumber(),
                       apr         : 61,
                       remainingCreditLimit: 1000,
                       balance: 0,
                       lastModifcationDate : '08/01/2020',
                       interestAccrued : 0,
                       totalAmountDue: 0])
        then:
        assert response.status == 500 : 'The new account outside the APR limit was invalidly created.'
    }

    def 'user should not be able to create a line of credit with a negative credit limit value'() {
        def response = client.post(path : 'api/v1/credit',
                contentType: JSON,
                body: [accountNumber        : firstClientData.getAccountNumber(),
                       apr                  : firstClientData.getAPR(),
                       remainingCreditLimit : -1,
                       balance              : 0,
                       lastModifcationDate  : '08/01/2020',
                       interestAccrued      : 0,
                       totalAmountDue       : 0])
        then:
        assert response.status == 500 : 'The new account with a negative credit limit was invalidly created.'
    }

    def 'user should not be able to create a line of credit with a negative APR value'() {
        def response = client.post(path : 'api/v1/credit',
                contentType: JSON,
                body: [accountNumber        : firstClientData.getAccountNumber(),
                       apr                  : -35,
                       remainingCreditLimit : 1000,
                       balance              : 0,
                       lastModifcationDate  : '08/01/2020',
                       interestAccrued      : 0,
                       totalAmountDue       : 0])
        then:
        assert response.status == 500 : 'The new account with a negative APR was invalidly created.'
    }

    def 'user should not be able to withdraw an amount greater than the current credit limit'() {
        when:
        def response = client.post(path : 'api/v1/credit',
                contentType: JSON,
                body: [accountNumber        : firstClientData.getAccountNumber(),
                       apr                  : firstClientData.getAPR(),
                       remainingCreditLimit : 1000,
                       balance              : 0,
                       lastModifcationDate  : '08/01/2020',
                       interestAccrued      : 0,
                       totalAmountDue       : 0])
        then:
        assert response.status == 201 : 'The new credit was not created. Please try again'

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

    def 'user should not be able to write data to an invalid url' () {
        def response = client.post(path : 'api/v1/creditInvalidURL',
                contentType: JSON,
                body: [accountNumber        : firstClientData.getAccountNumber(),
                       apr                  : firstClientData.getAPR(),
                       remainingCreditLimit : 1000,
                       balance              : 0,
                       lastModifcationDate  : '08/01/2020',
                       interestAccrued      : 0,
                       totalAmountDue       : 0])
        then:
        assert response.status == 404 : 'The connection to an invalid URL was invalidly successful'
    }

    def 'user should not be able to write data while using an invalid request format - wrong JSON keys' () {
        def response = client.post(path : 'api/v1/credit',
                contentType: JSON,
                body: [badAccountNumber        : firstClientData.getAccountNumber(),
                       badapr                  : firstClientData.getAPR(),
                       badRemainingCreditLimit : 1000,
                       balance                 : 0,
                       lastModifcationDate     : '08/01/2020',
                       interestAccrued         : 0,
                       totalAmountDue          : 0])
        then:
        assert response.status == 400 : 'The write using an invalid request was invalidly successful'
    }

    def 'user should not be able to write data while using an invalid request format - missing JSON keys' () {
        def response = client.post(path : 'api/v1/credit',
                contentType: JSON,
                body: [badRemainingCreditLimit : 1000,
                       balance                 : 0,
                       lastModifcationDate     : '08/01/2020',
                       interestAccrued         : 0,
                       totalAmountDue          : 0])
        then:
        assert response.status == 400 : 'The write using an invalid request was invalidly successful'
    }

//TODO: extra edge cases

    // authorization required access this API? If so test 401/403 error
}