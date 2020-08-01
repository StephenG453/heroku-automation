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
