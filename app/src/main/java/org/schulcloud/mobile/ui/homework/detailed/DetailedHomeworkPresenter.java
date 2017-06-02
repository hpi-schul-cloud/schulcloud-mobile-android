package org.schulcloud.mobile.ui.homework.detailed;

import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.model.Comment;
import org.schulcloud.mobile.data.model.Submission;
import org.schulcloud.mobile.injection.ConfigPersistent;
import org.schulcloud.mobile.ui.base.BasePresenter;
import org.schulcloud.mobile.util.RxUtil;

import javax.inject.Inject;

import io.realm.RealmList;

@ConfigPersistent
public class DetailedHomeworkPresenter extends BasePresenter<DetailedHomeworkMvpView> {

    @Inject
    public DetailedHomeworkPresenter(DataManager dataManager) {
        mDataManager = dataManager;
    }

    @Override
    public void attachView(DetailedHomeworkMvpView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (mSubscription != null) mSubscription.unsubscribe();
    }

    /**
     * loads a specific homework for a given id.
     * @param homeworkId given id for referencing.
     */
    public void loadHomework(String homeworkId) {
        checkViewAttached();
        getMvpView().showHomework(mDataManager.getHomeworkForId(homeworkId));
    }

    /**
     * loads a specific submission containing the comments.
     * @param homeworkId needed to reference the submission.
     */
    public void loadComments(String homeworkId) {
        checkViewAttached();
        RxUtil.unsubscribe(mSubscription);
        Submission submission = null;
        submission = mDataManager.getSubmissionForId(homeworkId);
        if (submission == null) {
            submission = new Submission();
            submission.comment = "Hausaufgabe noch nicht eingereicht.";
            submission.comments = new RealmList<>();
            Comment comment = new Comment();
            comment.comment = "Derzeit keine Kommentare";
            submission.comments.add(comment);
        }
        getMvpView().showSubmission(submission);
    }
}
