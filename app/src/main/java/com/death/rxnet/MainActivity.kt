package com.death.rxnet

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    private fun map() {
        loader.visibility = View.VISIBLE
        Rx2AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAnUser/{userId}")
            .addPathParameter("userId", "1")
            .build()
            .getObjectObservable(ApiUser::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                return@map User(it)
            }.subscribe(object : Observer<User> {
                override fun onComplete() {
                    loader.visibility = View.GONE
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: User) {
                    Log.e("User", t.toString())
                    Toast.makeText(this@MainActivity, "Name : ${t.firstname}", Toast.LENGTH_LONG).show()
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    Toast.makeText(this@MainActivity, e.localizedMessage, Toast.LENGTH_LONG).show()
                    loader.visibility = View.GONE
                }

            })
    }

    private fun zip() {
        findUsersWhoLovesBoth()
    }

    private fun flatMapAndFilter() {
        getAllFriendsObservable()
            .flatMap {
                return@flatMap Observable.fromIterable(it)
            }
    }

    private fun getAllFriendsObservable():Observable<List<User>>{
        return Rx2AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllFriends/{userId}")
            .addPathParameter("userId", "1")
            .build()
            .getObjectListObservable(User::class.java)
    }

    private fun cricketFans(): Observable<List<User>> {
        return Rx2AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllCricketFans")
            .build()
            .getObjectListObservable(User::class.java)
    }

    private fun footballFans(): Observable<List<User>> {
        return Rx2AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllFootballFans")
            .build()
            .getObjectListObservable(User::class.java)
    }

    private fun findUsersWhoLovesBoth() {
        loader.visibility = View.VISIBLE

        Observable.zip(cricketFans(), footballFans(),
            BiFunction<List<User>, List<User>, List<User>> { cricketFans, footballFans ->
                return@BiFunction filterUserWhoLovesBoth(cricketFans, footballFans)
            }).subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<User>> {
                override fun onComplete() {
                    loader.visibility = View.GONE
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: List<User>) {
                    val userNames = StringBuffer()
                    for(user in t){
                        userNames.append(user.firstname+"\n")
                    }
                    Toast.makeText(this@MainActivity,userNames, Toast.LENGTH_LONG).show()
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    Toast.makeText(this@MainActivity, e.localizedMessage, Toast.LENGTH_LONG).show()
                    loader.visibility = View.GONE
                }

            })

    }

    private fun filterUserWhoLovesBoth(cricketFans: List<User>, footballFans: List<User>): List<User> {

        return footballFans.filter {
            cricketFans.contains(it)
        }

        /**
         * this is the beauty of kotlin
         */
        /*
        val userWhoLovesBoth = ArrayList<User>()

        for (footballFan in footballFans) {
            if (cricketFans.contains(footballFan)) {
                userWhoLovesBoth.add(footballFan)
            }
        }

        return userWhoLovesBoth
        */
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(this).inflate(R.menu.menu_operations, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.map -> {
                map()
            }
            R.id.zip -> {
                zip()
            }
            R.id.flatMapAndFilter->{
                flatMapAndFilter()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
