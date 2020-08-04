import groovyx.net.http.*
import groovyx.net.http.*
import spock.lang.Shared
import spock.lang.Specification
import static groovyx.net.http.ContentType.JSON

class E2EHappyPath extends Specification {

    FirstClientData firstClientData
    @Shared def client

    def setup() {
        client = new RESTClient('http://credit-test.herokuapp.com/')
        firstClientData = new FirstClientData()
    }

    def 'user can create a new line of credit'() {
        when:
        def response = client.post(path: 'api/v1/credit',
                contentType: JSON,
                body: [accountNumber       : firstClientData.getAccountNumber(),
                       apr                 : firstClientData.getAPR(),
                       remainingCreditLimit: 1000,
                       balance             : 0,
                       lastModifcationDate : '08/01/2020',
                       interestAccrued     : 0,
                       totalAmountDue      : 0])
        then:
        assert response.status == 201: 'The new credit was not created. Please try again'
    }

    def 'user can retrieve data after creating a new line' () {
        when:
        def response = client.get(path: 'api/v1/credit/' + firstClientData.getAccountNumber())

        then:
        checkForCorrectDataInResponse(response, 200, 35, 1000, 0,
                0, 0)
    }

    def 'user can make one withdrawal' () {
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

    def 'user can retrieve data after making a withdrawal' () {
        when:
        def response = client.get(path: 'api/v1/credit/' + firstClientData.getAccountNumber())

        then:
        checkForCorrectDataInResponse(response,200, 35, 500, 500,
                14.38, 514.38)
    }

//    def 'user can create a new line of credit, make one payment, make one withdrawal, and retrieve info after 30 days'() {
//        when:
//        def response = client.post(path: 'api/v1/credit',
//                contentType: JSON,
//                body: [accountNumber        : firstClientData.getAccountNumber(),
//                       apr                  : firstClientData.getAPR(),
//                       remainingCreditLimit : 1000,
//                       balance              : 0,
//                       lastModifcationDate  : '08/01/2020',
//                       interestAccrued      : 0,
//                       totalAmountDue       : 0])
//        then:
//        assert response.status == 201 : 'the new credit line was not created'
//
//        and:
//        client.get(path: 'api/v1/credit/' + firstClientData.getAccountNumber())
//
//        then:
//        checkForCorrectDataInResponse(response,200, 35, 1000, 0,
//                0, 0)
//
//        and:
//        client.put(path: 'api/v1/credit/' + firstClientData.getAccountNumber(),
//                contentType: JSON,
//                body: [accountNumber        : firstClientData.getAccountNumber(),
//                       apr                  : firstClientData.getAPR(),
//                       remainingCreditLimit : 500,
//                       balance              : 500,
//                       lastModifcationDate  : '08/01/2020',
//                       interestAccrued      : 0,
//                       totalAmountDue       : 500])
//        then:
//        assert response.status == 201 : 'update was unsuccessful and new resource was not created'
//
////        and:
////        response = client.get(path: 'api/v1/credit/' + firstClientData.getAccountNumber())
////
////        then:
////        checkForCorrectDataInResponse(response,200, 35, 500, 500,
////                0, 500)
//
//        and:
//        client.put(path: 'api/v1/credit/' + firstClientData.getAccountNumber(),
//                contentType: JSON,
//                body: [accountNumber        : firstClientData.getAccountNumber(),
//                       apr                  : firstClientData.getAPR(),
//                       remainingCreditLimit : 700,
//                       balance              : 300,
//                       lastModifcationDate  : '08/15/2020',
//                       interestAccrued      : 7.19,
//                       totalAmountDue       : 500])
//        then:
//        assert response.status == 201 : 'update was unsuccessful and new resource was not created'
//
//        and:
//        client.get(path: 'api/v1/credit/' + firstClientData.getAccountNumber())
//
//        then:
//        checkForCorrectDataInResponse(response, 200, 35, 700, 300,
//                7.19, 300)
//
//        and:
//        client.put(path: 'api/v1/credit/' + firstClientData.getAccountNumber(),
//                contentType: JSON,
//                body: [accountNumber        : firstClientData.getAccountNumber(),
//                       apr                  : firstClientData.getAPR(),
//                       remainingCreditLimit : 600,
//                       balance              : 400,
//                       lastModifcationDate  : '08/25/2020',
//                       interestAccrued      : 10.07,
//                       totalAmountDue       : 400])
//        then:
//        assert response.status == 201 : 'update was unsuccessful and new resource was not created'
//
//        and:
//        client.get(path: 'api/v1/credit/' + firstClientData.getAccountNumber())
//
//        then:
//        checkForCorrectDataInResponse(response, 200, 35, 600, 400,
//                10.07, 400)
//
//        and:
//        client.get(path: 'api/v1/credit/' + firstClientData.getAccountNumber())
//
//        then:
//        checkForCorrectDataInResponse(response, 200, 35, 600, 400,
//                11.99, 411.99)
//    }

    void checkForCorrectDataInResponse(response, responseStatus, apr, remainingCreditLimit,
                                       balance, interestAccrued, totalAmountDue) {
        assert response.status == responseStatus : 'could not retrieve current account information'
        assert response['apr'] == apr : 'The expected APR is incorrect'
        assert response['remainingCreditLimit'] == remainingCreditLimit : 'The expected credit limit is incorrect'
        assert response['balance'] == balance : 'The expected balance is incorrect'
        assert response['interestAccrued'] == interestAccrued : 'The expected interest accrued is incorrect'
        assert response['totalAmountDue'] == totalAmountDue : 'The expected total amount due is incorrect'
    }

//    void getResponse() {
//        client
//    }
}