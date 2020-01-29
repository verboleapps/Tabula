package com.verbole.dcad.tabula.RoomDatabase;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

public class BaseLatinRepository {
    private MutableLiveData<List<Baseentrees>> searchResults =
            new MutableLiveData<>();

    private BaseentreesDAO baseentreesDAO;
    private LiveData<List<Baseentrees>> allEntrees;

    public BaseLatinRepository(Application application) {
        BaseLatinRoomDatabase db;
        db = BaseLatinRoomDatabase.getDatabase(application);
        baseentreesDAO = db.baseentreesDAO();
        allEntrees = baseentreesDAO.getAllEntrees();
        Log.d("REPOSIT","base lat repo " + " ??");
    }


    private void asyncFinished(List<Baseentrees> results) {

        searchResults.setValue(results);

    }

    private static class QueryAsyncTask extends
            AsyncTask<Integer, Void, List<Baseentrees>> {

        private BaseentreesDAO asyncTaskDao;
        private BaseLatinRepository delegate = null;

        QueryAsyncTask(BaseentreesDAO dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected List<Baseentrees> doInBackground(final Integer... params) {
            return asyncTaskDao.findEntree(params[0]);
        }

        @Override
        protected void onPostExecute(List<Baseentrees> result) {
            delegate.asyncFinished(result);
        }
    }

    public LiveData<List<Baseentrees>> getAllEntrees() {
        if (allEntrees != null) {
            //Log.d("REPOSIT","async fin " + allEntrees.getValue().size() + "??");
        }
        else {
            Log.d("REPOSIT","async fin ??");
        }

        return allEntrees;
    }

    public MutableLiveData<List<Baseentrees>> getSearchResults() {
        return searchResults;
    }

    public void findEntree(Integer indice) {
        QueryAsyncTask task = new QueryAsyncTask(baseentreesDAO);
        task.delegate = this;
        task.execute(indice);
    }
}
