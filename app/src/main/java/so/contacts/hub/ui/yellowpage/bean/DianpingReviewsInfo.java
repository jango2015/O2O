package so.contacts.hub.ui.yellowpage.bean;

/**
 * 黄页详情（大众点评）评论
 * @author Michael
 *
 */
public class DianpingReviewsInfo  implements java.io.Serializable {

	private int review_id;  //单条点评ID
	
	private String text_excerpt;  //点评文字片断，前50字

	private double review_rating;  //点评作者提供的星级评分，5.0代表五星，4.5代表四星半，依此类推
	
	private String review_url;  //点评页面链接
	
	public DianpingReviewsInfo(){
		
	}

	public int getReview_id() {
		return review_id;
	}

	public void setReview_id(int review_id) {
		this.review_id = review_id;
	}

	public String getText_excerpt() {
		return text_excerpt;
	}

	public void setText_excerpt(String text_excerpt) {
		this.text_excerpt = text_excerpt;
	}

	public double getReview_rating() {
		return review_rating;
	}

	public void setReview_rating(double review_rating) {
		this.review_rating = review_rating;
	}

	public String getReview_url() {
		return review_url;
	}

	public void setReview_url(String review_url) {
		this.review_url = review_url;
	}
	
	
	
	
}
