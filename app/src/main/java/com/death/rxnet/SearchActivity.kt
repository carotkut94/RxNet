package com.death.rxnet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search.*
import java.util.concurrent.TimeUnit

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        RxSearchViewObservable.fromView(searchView)
            .debounce(300, TimeUnit.MILLISECONDS)
            .filter {query->
                if(query.isEmpty()){
                    return@filter false
                }
                return@filter true
            }
            .distinctUntilChanged()
            .switchMap {
                dataFromNetwork(it)
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(Consumer {
                Toast.makeText(this@SearchActivity, it, Toast.LENGTH_LONG).show()
            })
    }

    private fun dataFromNetwork(query: String): Observable<String> {
        return Observable.just(true)
            .delay(2, TimeUnit.SECONDS)
            .map{
                return@map query
            }
    }
}
