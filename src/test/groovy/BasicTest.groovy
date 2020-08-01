import groovyx.net.http.*
import groovyx.net.http.*
import spock.lang.Specification
import static groovyx.net.http.ContentType.JSON

class BasicTest extends Specification {

    def client = new RESTClient('http://credit-test.herokuapp.com/')

    def 'user can create a new line of credit' () {
        def response = client.post(path : 'api/v2/credit',
                                   contentType : JSON,
                                    body : [id: 'testID01',
                                            apr: 35,
                                            creditAmount: 1000])
        assert response.status == 201 : 'the new credit was not created'
    }
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