/**
 * 
 */
package so.contacts.hub.businessbean;


/**
 * @author Acher
 *
 */
public class DownloadStatus {

	public int status = -1;  // see DownloadManager.STATUS_SUCCESSFUL
	public long id = -1;
	public long bytes_so_far;
	public long total_size;
	public String local_uri;

}
