import com._17od.upm.util.Validator;
import junit.framework.TestCase;

public class PeteTest extends TestCase 
{
	private static final String errMsg = "";
	
	
	//Testing that the Validator checkArgument does not 
	//throw an error when valid parameters are passed.
	public void testCheckArg() 
	{
        Validator.checkArgument( 1 == 1, errMsg);
    }
	
	//And testing that Validator checkArgument does
	//throw an error when incorrect parameters are passed
	public void testBadCheckArg() 
	{
        try 
        {
            Validator.checkArgument(1 == 2, errMsg);
            fail("Should not reach this line");
        } 
        catch (IllegalArgumentException e) 
        {
            // Should reach this line
        }
    }
}
    
    
    
    /*
    
    
    public void testNonNullDoesntThrowError() {
        Validator.checkNotNull("I'm not null", errorMsg);
    }
    
    public void testNullThrowsError() {
        try {
            Validator.checkNotNull(null, errorMsg);
            fail("No error was thrown, but expected IllegalArg");
        } catch (IllegalArgumentException e) {
            // Do nothing, it was thrown as expected
        }
    }
}
*/