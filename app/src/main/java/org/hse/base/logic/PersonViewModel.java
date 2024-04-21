package org.hse.base.logic;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;

import org.hse.base.data.dataClasses.TimeResponse;
import org.hse.base.data.db.entities.GroupEntity;
import org.hse.base.data.db.entities.TeacherEntity;
import org.hse.base.data.db.entities.TimetableWithTeacherEntity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PersonViewModel extends AndroidViewModel {

    private final MutableLiveData<Date> currentTime = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private String TAG = "PersonViewModel";
    private DbRepo dbRepo;
    public PersonViewModel(@NonNull Application application) {
        super(application);
        dbRepo = new DbRepo(application);
    }

    public LiveData<List<GroupEntity>> getGroups() { return dbRepo.getGroups(); }

    public LiveData<List<TeacherEntity>> getTeachers() { return dbRepo.getTeachers(); }

    public LiveData<List<TimetableWithTeacherEntity>> getTimetablesWithTeacherByDateAndGroupId(Date date, int groupId) {
        return dbRepo.getTimetablesWithTeacherByDateAndGroupId(date, groupId);
    }

    public LiveData<List<TimetableWithTeacherEntity>> getTimetablesWithTeacherByDateAndTeacherId(Date date, int teacherId) {
        return dbRepo.getTimetablesWithTeacherByDateAndTeacherId(date, teacherId);
    }

    public LiveData<List<TimetableWithTeacherEntity>> getStudentsTimetableByDate(Date start, Date end, int groupId) {
        return dbRepo.getStudentsTimetableByDate(start, end, groupId);
    }

    public LiveData<List<TimetableWithTeacherEntity>> getTeacherTimetableByDate(Date start, Date end, int teacherId) {
        return dbRepo.getTeacherTimetableByDate(start, end, teacherId);
    }

    public LiveData<Date> getTime() { return currentTime; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void fetchTime() {
        String URL = "https://api.ipgeolocation.io/ipgeo?apiKey=b03018f75ed94023a005637878ec0977";
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(URL).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                parseResponse(response);
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                errorMessage.postValue(e.getMessage());
            }
        });
    }

    private void parseResponse(Response response) {
        Gson gson = new Gson();
        ResponseBody body = response.body();

        if (body == null) {
            return;
        }

        try {
            String string = body.string();
            Log.d(TAG, string);
            TimeResponse timeResponse = gson.fromJson(string, TimeResponse.class);
            String currentTimeVal = timeResponse.getTimeZone().getCurrentTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
            Date dateTime = simpleDateFormat.parse(currentTimeVal);
            currentTime.postValue(dateTime);
        } catch (Exception e) {
            Log.e(TAG, "" + e);
            Calendar calendar = Calendar.getInstance();
            Date localDateTime = calendar.getTime();
            errorMessage.postValue(e.getMessage());
            currentTime.postValue(localDateTime);
        }
    }

}
