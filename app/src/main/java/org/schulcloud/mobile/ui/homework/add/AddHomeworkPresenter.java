package org.schulcloud.mobile.ui.homework.add;

import android.support.annotation.NonNull;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.local.PreferencesHelper;
import org.schulcloud.mobile.data.model.Course;
import org.schulcloud.mobile.data.model.CurrentUser;
import org.schulcloud.mobile.data.model.requestBodies.AddHomeworkRequest;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

@ConfigPersistent
public class AddHomeworkPresenter extends BasePresenter<AddHomeworkMvpView> {

    private DataManager mDataManager;
    private PreferencesHelper mPreferencesHelper;

    private Subscription mSubscription;
    private Subscription mCoursesSubscription;
    private Subscription mCurrentUserSubscription;

    private List<Course> mCourses;

    private DateFormat mDateFormat;

    @Inject
    public AddHomeworkPresenter(DataManager dataManager, PreferencesHelper preferencesHelper) {
        mDataManager = dataManager;
        mPreferencesHelper = preferencesHelper;

        mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }

    @Override
    protected void onViewDetached() {
        super.onViewDetached();
        RxUtil.unsubscribe(mSubscription);
        RxUtil.unsubscribe(mCoursesSubscription);
        RxUtil.unsubscribe(mCurrentUserSubscription);
    }

    public void loadData() {
        mCourses = new ArrayList<>();
        mCourses.add(null);
        List<String> names = new ArrayList<>();
        names.add(null);

        RxUtil.unsubscribe(mCoursesSubscription);
        mCoursesSubscription = mDataManager.getCourses()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        courses -> {
                            mCourses.addAll(courses);
                            for (Course course : courses)
                                names.add(course.name);
                            sendToView(view -> view.setCourses(names));
                        },
                        throwable -> {
                            Timber.e(throwable, "There was an error loading the courses.");
                            sendToView(AddHomeworkMvpView::showCourseLoadingError);
                        });

        RxUtil.unsubscribe(mCurrentUserSubscription);
        mCurrentUserSubscription = mDataManager.getCurrentUser()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        currentUser -> sendToView(view -> view.setCanCreatePublic(currentUser
                                .hasPermission(CurrentUser.PERMISSION_COURSE_EDIT))),
                        throwable -> Timber.e(throwable,
                                "There was an error loading the current user."));
    }

    public void addHomework(@NonNull String name, int courseIndex, boolean isPrivate,
            @NonNull String description, @NonNull Calendar availableDate, @NonNull Calendar dueDate,
            boolean publicSubmissions) {
        if (name.isEmpty())
            getViewOrThrow().showNameEmpty();
        else if (!dueDate.after(availableDate))
            getViewOrThrow().showInvalidDates();
        else {
            Course course = mCourses.get(courseIndex);
            AddHomeworkRequest addHomeworkRequest = new AddHomeworkRequest(
                    mPreferencesHelper.getCurrentSchoolId(),
                    mPreferencesHelper.getCurrentUserId(),
                    name,
                    course == null ? null : course._id,
                    description,
                    mDateFormat.format(availableDate.getTime()),
                    mDateFormat.format(dueDate.getTime()),
                    publicSubmissions,
                    isPrivate);

            RxUtil.unsubscribe(mSubscription);
            mSubscription = mDataManager.addHomework(addHomeworkRequest)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            addHomeworkResponse -> sendToView(
                                    AddHomeworkMvpView::reloadHomeworkList),
                            throwable -> sendToView(AddHomeworkMvpView::showSaveError),
                            () -> sendToView(AddHomeworkMvpView::showHomeworkSaved));
        }
    }
}
