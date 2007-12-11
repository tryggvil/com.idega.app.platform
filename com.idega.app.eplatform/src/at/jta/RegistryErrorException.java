// $Id: RegistryErrorException.java,v 1.1 2007-12-11 02:35:40 eiki Exp $
package at.jta;

import java.io.IOException;

/*******************************************************************************************************************************
 *
 * <p>Title: class for throwing exceptions </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: Taschek Joerg</p>
 *
 * @author Taschek Joerg
 * @version 1.0
 ******************************************************************************************************************************/
public class RegistryErrorException
    extends IOException
{

  /******************************************************************************************************************************
   * Constructor with message to throw
   * @param reason String
   *****************************************************************************************************************************/
  public RegistryErrorException(String reason)
  {
    super(reason);
  }
}
