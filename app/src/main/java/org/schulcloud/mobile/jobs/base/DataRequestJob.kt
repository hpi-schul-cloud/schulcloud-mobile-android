package org.schulcloud.mobile.jobs.base

import android.util.Log
import io.realm.RealmModel
import io.realm.RealmQuery
import org.schulcloud.mobile.BuildConfig
import org.schulcloud.mobile.models.Sync
import org.schulcloud.mobile.models.base.HasId
import org.schulcloud.mobile.network.ApiService
import org.schulcloud.mobile.network.ApiServiceInterface
import org.schulcloud.mobile.network.FeathersResponse
import org.schulcloud.mobile.utils.it
import retrofit2.Call
import ru.gildor.coroutines.retrofit.awaitResponse


