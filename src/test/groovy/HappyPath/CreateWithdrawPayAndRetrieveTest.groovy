package HappyPath

import ClientData.FirstClientData
import groovyx.net.http.RESTClient
import spock.lang.Shared
import spock.lang.Specification
import static groovyx.net.http.ContentType.JSON

class CreateWithdrawPayAndRetrieveTest extends Specification {

    @Shared FirstClientData firstClientData
    @Shared def client

    def setupSpec() {
        client = new RESTClient('http://credit-test.herokuapp.com/')
        firstClientData = new FirstClientData()
    }

    def 'user can create a new line of credit'() {
        when:
        def response = createNewCreditLine()

        then:
        assert response.status == 201: 'the new credit line was not created'
    }

    def 'user can retrieve data after creating a new credit line' () {
        when:
        def response = client.get(path: 'api/v1/credit/' + firstClientData.getAccountNumber())

        then:
        checkForCorrectDataInResponse(response, 200, 35, 1000, 0,
                0, 0)
    }

    def 'user can make a withdrawal' () {
        when:
        def response = client.put(path: 'api/v1/credit/' + firstClientData.getAccountNumber(),
                contentType: JSON,
                body: [accountNumber       : firstClientData.getAccountNumber(),
                       apr                 : firstClientData.getAPR(),
                       remainingCreditLimit: 500,
                       balance             : 500,
                       lastModifcationDate : '08/01/2020',
                       interestAccrued     : 0,
                       totalAmountDue      : 500])
        then:
        assert response.status == 201: 'update was unsuccessful and new resource was not created'
    }

    def 'user can retrieve data after making a withdrawal'() {
        when:
        def response = client.get(path: 'api/v1/credit/' + firstClientData.getAccountNumber())

        then:
        checkForCorrectDataInResponse(response, 200, 35, 500, 500,
                0, 500)
    }

    def 'user can make a payment' () {
        when:
        def response = client.put(path: 'api/v1/credit/' + firstClientData.getAccountNumber(),
                contentType: JSON,
                body: [accountNumber       : firstClientData.getAccountNumber(),
                       apr                 : firstClientData.getAPR(),
                       remainingCreditLimit: 700,
                       balance             : 300,
                       lastModifcationDate : '08/15/2020',
                       interestAccrued     : 7.19,
                       totalAmountDue      : 300])
        then:
        assert response.status == 201: 'update was unsuccessful and new resource was not created'
    }

    def 'user can retrieve data after making a payment'() {
        when:
        def response = client.get(path: 'api/v1/credit/' + firstClientData.getAccountNumber())

        then:
        checkForCorrectDataInResponse(response, 200, 35, 700, 300,
                7.19, 300)
    }

    def 'user can make a withdrawal again' () {
        when:
        def response = client.put(path: 'api/v1/credit/' + firstClientData.getAccountNumber(),
                contentType: JSON,
                body: [accountNumber       : firstClientData.getAccountNumber(),
                       apr                 : firstClientData.getAPR(),
                       remainingCreditLimit: 600,
                       balance             : 400,
                       lastModifcationDate : '08/25/2020',
                       interestAccrued     : 10.07,
                       totalAmountDue      : 400])
        then:
        assert response.status == 201: 'update was unsuccessful and new resource was not created'
    }

    def 'user can retrieve data after making a second withdrawal'() {
        when:
        def response = client.get(path: 'api/v1/credit/' + firstClientData.getAccountNumber())

        then:
        checkForCorrectDataInResponse(response, 200, 35, 600, 400,
                10.07, 400)
    }

    def 'user can get data at end of month' () {
        when:
        def response = client.get(path: 'api/v1/credit/' + firstClientData.getAccountNumber())

        then:
        checkForCorrectDataInResponse(response, 200, 35, 600, 400,
                11.99, 411.99)
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

    def createNewCreditLine() {
        def response = client.post(path: 'api/v1/credit',
                contentType: JSON,
                body: [accountNumber       : firstClientData.getAccountNumber(),
                       apr                 : firstClientData.getAPR(),
                       remainingCreditLimit: 1000,
                       balance             : 0,
                       lastModifcationDate : '08/01/2020',
                       interestAccrued     : 0,
                       totalAmountDue      : 0])
        return response
    }
}