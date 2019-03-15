package com.death.rxnet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rx2androidnetworking.Rx2AndroidNetworking
import io.github.kbiakov.codeview.adapters.Options
import io.github.kbiakov.codeview.highlight.ColorTheme
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapCode.setOptions(Options.get(this).withLanguage("kotlin").withTheme(ColorTheme.MONOKAI))
        mapCode.setCode(CodeProvide.mapCode())


        zipCode.setOptions(Options.get(this).withLanguage("kotlin").withTheme(ColorTheme.MONOKAI))
        zipCode.setCode(CodeProvide.zipCode())

        flatMapFilter.setOptions(Options.get(this).withLanguage("kotlin").withTheme(ColorTheme.MONOKAI))
        flatMapFilter.setCode(CodeProvide.flatMapFilter())

        take.setOptions(Options.get(this).withLanguage("kotlin").withTheme(ColorTheme.MONOKAI))
        take.setCode(CodeProvide.takeCode())

        flatMap.setOptions(Options.get(this).withLanguage("kotlin").withTheme(ColorTheme.MONOKAI))
        flatMap.setCode(CodeProvide.flatMapCode())

        flatMapZip.setOptions(Options.get(this).withLanguage("kotlin").withTheme(ColorTheme.MONOKAI))
        flatMapZip.setCode(CodeProvide.flatMapWithZipCode())

    }


    private fun map() {
        loader.visibility = View.VISIBLE
        val subscribe = Rx2AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAnUser/{userId}")
            .addPathParameter("userId", "1")
            .build()
            .getObjectObservable(ApiUser::class.java)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                return@map User(it)
            }.subscribe(
                { user ->
                    Log.e("User", user.toString())
                    Toast.makeText(this@MainActivity, "Name : ${user.firstname}", Toast.LENGTH_LONG).show()
                },
                { error ->
                    error.printStackTrace()
                    loader.visibility = View.GONE
                    Toast.makeText(this@MainActivity, error.localizedMessage, Toast.LENGTH_LONG).show()
                },
                { loader.visibility = View.GONE }
            )
    }

    private fun zip() {
        findUsersWhoLovesBoth()
    }

    private fun take() {
        loader.visibility = View.VISIBLE
        val usernames = StringBuilder()
        val subscribe = getUserListObservable().flatMap {
            return@flatMap Observable.fromIterable(it)
        }
            .take(4)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { user -> usernames.append(user.firstname + "\n") },
                { error ->
                    error.printStackTrace()
                    loader.visibility = View.GONE
                    Toast.makeText(this@MainActivity, error.localizedMessage, Toast.LENGTH_LONG).show()
                },
                {
                    loader.visibility = View.GONE
                    Toast.makeText(this@MainActivity, usernames, Toast.LENGTH_LONG).show()
                }
            )
    }

    private fun getUserListObservable(): Observable<List<User>> {
        return Rx2AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
            .addPathParameter("pageNumber", "0")
            .addQueryParameter("limit", "10")
            .build()
            .getObjectListObservable(User::class.java)
    }

    private fun getUserDetailObservable(id: Long): Observable<UserDetail> {
        return Rx2AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAnUserDetail/{userId}")
            .addPathParameter("userId", id.toString())
            .build()
            .getObjectObservable<UserDetail>(UserDetail::class.java)
    }

    private fun flatMap() {
        loader.visibility = View.VISIBLE
        val userDetails = StringBuilder()
        val subscribe = getUserListObservable()
            .flatMap {
                Observable.fromIterable(it)
            }
            .flatMap {
                return@flatMap getUserDetailObservable(it.id)
            }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    userDetails.append("User Details : ${it.firstname}, ${it.lastname} \n")
                },
                { error ->
                    error.printStackTrace()
                    loader.visibility = View.GONE
                },
                {
                    loader.visibility = View.GONE
                    Toast.makeText(this@MainActivity, userDetails.toString(), Toast.LENGTH_LONG).show()
                }
            )

    }

    private fun flatMapAndFilter() {
        val usernames = StringBuilder()
        loader.visibility = View.VISIBLE
        val subscribe = getAllFriendsObservable()
            .flatMap {
                return@flatMap Observable.fromIterable(it)
            }
            .filter {
                return@filter it.isFollowing
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    usernames.append(it.firstname + "\n")
                },
                { error ->
                    error.printStackTrace()
                    Toast.makeText(this@MainActivity, error.localizedMessage, Toast.LENGTH_LONG).show()
                    loader.visibility = View.GONE
                },
                {
                    loader.visibility = View.GONE
                    Toast.makeText(this@MainActivity, usernames, Toast.LENGTH_SHORT).show()
                })
    }


    private fun getAllFriendsObservable(): Observable<List<User>> {
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

        val subscribe = Observable.zip(cricketFans(), footballFans(),
            BiFunction<List<User>, List<User>, List<User>> { cricketFans, footballFans ->
                return@BiFunction filterUserWhoLovesBoth(cricketFans, footballFans)
            }).subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    val userNames = StringBuffer()
                    for (user in it) {
                        userNames.append(user.firstname + "\n")
                    }
                    Toast.makeText(this@MainActivity, userNames, Toast.LENGTH_LONG).show()
                },
                { error ->
                    error.printStackTrace()
                    Toast.makeText(this@MainActivity, error.localizedMessage, Toast.LENGTH_LONG).show()
                    loader.visibility = View.GONE
                },
                { loader.visibility = View.GONE }
            )

    }

    private fun flatMapWithZip() {
        loader.visibility = View.VISIBLE
        val usernames = StringBuilder()
        val subscribe = getUserListObservable()
            .flatMap {
                Observable.fromIterable(it)
            }
            .flatMap {
                return@flatMap Observable.zip(getUserDetailObservable(it.id), Observable.just(it),
                    BiFunction<UserDetail, User, Pair<UserDetail, User>> { userDetail, user ->
                        return@BiFunction Pair(userDetail, user)
                    })
            }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    val userData = it.first
                    val user = it.second
                    usernames.append("${userData.id} = = = ${user.firstname}\n")
                },
                {
                    it.printStackTrace()
                },
                {
                    loader.visibility = View.GONE
                    Toast.makeText(this@MainActivity, usernames.toString(), Toast.LENGTH_LONG).show()
                }
            )
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
            R.id.flatMapAndFilter -> {
                flatMapAndFilter()
            }
            R.id.take -> {
                take()
            }
            R.id.flatMap -> {
                flatMap()
            }
            R.id.flatMapWithZip -> {
                flatMapWithZip()
            }
            R.id.search->{
                startActivity(Intent(this,SearchActivity::class.java))
            }

        }
        return super.onOptionsItemSelected(item)
    }

}
