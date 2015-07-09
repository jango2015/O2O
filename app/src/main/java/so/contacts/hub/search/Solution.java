package so.contacts.hub.search;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;

public class Solution {
    
	public static final int SOLUTION_HIT_LOCAL = 0;
	public static final int SOLUTION_HIT_SERVER = 1;
	public static final int SOLUTION_HIT_DEFAULT = 2;

    private int entry;

    private String inputKeyword;
    
    private String inputCity;
    
    private double inputLatitude;
    
    private double inputLongtitude;
    
    private List<SearchTask> taskList;
    
    private boolean hasMore;
        
    private Context activity;
    
    private Handler mainHandler;
    
    private int hit;
    
    public Solution() {
    }
    
    @Override
    public Solution clone() {
        Solution sol = new Solution();
        sol.entry = this.entry;
        sol.hit = this.hit;
        sol.inputKeyword = this.inputKeyword;
        sol.inputCity = this.inputCity;
        sol.inputLatitude = this.inputLatitude;
        sol.inputLongtitude = this.inputLongtitude;
        sol.hasMore = this.hasMore;
        sol.activity = this.activity;
        sol.mainHandler = this.mainHandler;
        sol.taskList = new ArrayList<SearchTask>();
        if( taskList != null ){
        	sol.taskList.addAll(taskList);
        }
        
        return sol;
    }
    
     public Solution cloneButAddTask(SearchTask task) {
        Solution sol = new Solution();
        sol.entry = this.entry;
        sol.hit = this.hit;
        sol.inputKeyword = this.inputKeyword;
        sol.inputCity = this.inputCity;
        sol.inputLatitude = this.inputLatitude;
        sol.inputLongtitude = this.inputLongtitude;
        sol.hasMore = this.hasMore;
        sol.activity = this.activity;
        sol.mainHandler = this.mainHandler;
        sol.taskList = new ArrayList<SearchTask>();
        sol.taskList.add(task);
        
        return sol;
    }   

    public Solution(Handler handler) {
        mainHandler = handler;
    }
    
    public Handler getMainHandler() {
        return mainHandler;
    }


    public void setMainHandler(Handler mainHandler) {
        this.mainHandler = mainHandler;
    }


    public String getInputKeyword() {
        return inputKeyword;
    }


    public void setInputKeyword(String inputKeyword) {
        this.inputKeyword = inputKeyword;
    }


    public List<SearchTask> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<SearchTask> taskList) {
        this.taskList = taskList;
    }
    
    public boolean isHasMore() {
        return hasMore;
    }


    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public String getInputCity() {
        return inputCity;
    }

    public void setInputCity(String inputCity) {
        this.inputCity = inputCity;
    }

    public double getInputLatitude() {
        return inputLatitude;
    }

    public void setInputLatitude(double inputLatitude) {
        this.inputLatitude = inputLatitude;
    }

    public double getInputLongtitude() {
        return inputLongtitude;
    }

    public void setInputLongtitude(double inputLongtitude) {
        this.inputLongtitude = inputLongtitude;
    }

    public int getEntry() {
        return entry;
    }

    public void setEntry(int entry) {
        this.entry = entry;
    }

    public Context getActivity() {
        return activity;
    }

    public void setActivity(Context activity) {
        this.activity = activity;
    }

    public int getHit() {
		return hit;
	}

	public void setHit(int hit) {
		this.hit = hit;
	}

	@Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Solution entry=").append(entry).append(" hit=").append(hit).append(" inputKeyword=").append(inputKeyword)
        .append(" inputCity=").append(inputCity)
        .append(" inputLatitude=").append(inputLatitude)
        .append(" inputLongtitude=").append(inputLongtitude).append("\n");
        for(int i=0; i<taskList.size(); i++) {
            sb.append("\t").append(taskList.get(i).toString()).append("\n");
        }
        
        return sb.toString();
    }

}
