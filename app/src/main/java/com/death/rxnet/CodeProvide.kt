package com.death.rxnet

object CodeProvide{
    fun mapCode():String{
        return "private fun map() {\n" +
                "        loader.visibility = View.VISIBLE\n" +
                "        val subscribe = Rx2AndroidNetworking.get(\"https://fierce-cove-29863.herokuapp.com/getAnUser/{userId}\")\n" +
                "            .addPathParameter(\"userId\", \"1\")\n" +
                "            .build()\n" +
                "            .getObjectObservable(ApiUser::class.java)\n" +
                "            .subscribeOn(Schedulers.io())\n" +
                "            .observeOn(AndroidSchedulers.mainThread())\n" +
                "            .map {\n" +
                "                return@map User(it)\n" +
                "            }.subscribe(\n" +
                "                { user ->\n" +
                "                    Log.e(\"User\", user.toString())\n" +
                "                    Toast.makeText(this@MainActivity, \"Name\" : \${user.firstname}\", Toast.LENGTH_LONG).show()\n" +
                "                },\n" +
                "                { error ->\n" +
                "                    error.printStackTrace()\n" +
                "                    loader.visibility = View.GONE\n" +
                "                    Toast.makeText(this@MainActivity, error.localizedMessage, Toast.LENGTH_LONG).show()\n" +
                "                },\n" +
                "                { loader.visibility = View.GONE }\n" +
                "            )\n" +
                "    }"
    }

    fun zipCode():String{
        return "private fun findUsersWhoLovesBoth() {\n" +
                "        loader.visibility = View.VISIBLE\n" +
                "\n" +
                "        val subscribe = Observable.zip(cricketFans(), footballFans(),\n" +
                "            BiFunction<List<User>, List<User>, List<User>> { cricketFans, footballFans ->\n" +
                "                return@BiFunction filterUserWhoLovesBoth(cricketFans, footballFans)\n" +
                "            }).subscribeOn(Schedulers.newThread())\n" +
                "            .observeOn(AndroidSchedulers.mainThread())\n" +
                "            .subscribe(\n" +
                "                {\n" +
                "                    val userNames = StringBuffer()\n" +
                "                    for (user in it) {\n" +
                "                        userNames.append(user.firstname + \"\\n\")\n" +
                "                    }\n" +
                "                    Toast.makeText(this@MainActivity, userNames, Toast.LENGTH_LONG).show()\n" +
                "                },\n" +
                "                { error ->\n" +
                "                    error.printStackTrace()\n" +
                "                    Toast.makeText(this@MainActivity, error.localizedMessage, Toast.LENGTH_LONG).show()\n" +
                "                    loader.visibility = View.GONE\n" +
                "                },\n" +
                "                { loader.visibility = View.GONE }\n" +
                "            )\n" +
                "\n" +
                "    }"
    }

    fun flatMapFilter():String{
        return "private fun flatMapAndFilter() {\n" +
                "        val usernames = StringBuilder()\n" +
                "        loader.visibility = View.VISIBLE\n" +
                "        val subscribe = getAllFriendsObservable()\n" +
                "            .flatMap {\n" +
                "                return@flatMap Observable.fromIterable(it)\n" +
                "            }\n" +
                "            .filter {\n" +
                "                return@filter it.isFollowing\n" +
                "            }\n" +
                "            .subscribeOn(Schedulers.io())\n" +
                "            .observeOn(AndroidSchedulers.mainThread())\n" +
                "            .subscribe(\n" +
                "                {\n" +
                "                    usernames.append(it.firstname + \"\\n\")\n" +
                "                },\n" +
                "                { error ->\n" +
                "                    error.printStackTrace()\n" +
                "                    Toast.makeText(this@MainActivity, error.localizedMessage, Toast.LENGTH_LONG).show()\n" +
                "                    loader.visibility = View.GONE\n" +
                "                },\n" +
                "                {\n" +
                "                    loader.visibility = View.GONE\n" +
                "                    Toast.makeText(this@MainActivity, usernames, Toast.LENGTH_SHORT).show()\n" +
                "                })\n" +
                "    }"
    }

    fun takeCode():String{
        return "private fun take() {\n" +
                "        loader.visibility = View.VISIBLE\n" +
                "        val usernames = StringBuilder()\n" +
                "        val subscribe = getUserListObservable().flatMap {\n" +
                "            return@flatMap Observable.fromIterable(it)\n" +
                "        }\n" +
                "            .take(4)\n" +
                "            .subscribeOn(Schedulers.io())\n" +
                "            .observeOn(AndroidSchedulers.mainThread())\n" +
                "            .subscribe(\n" +
                "                { user -> usernames.append(user.firstname + \"\\n\") },\n" +
                "                { error ->\n" +
                "                    error.printStackTrace()\n" +
                "                    loader.visibility = View.GONE\n" +
                "                    Toast.makeText(this@MainActivity, error.localizedMessage, Toast.LENGTH_LONG).show()\n" +
                "                },\n" +
                "                {\n" +
                "                    loader.visibility = View.GONE\n" +
                "                    Toast.makeText(this@MainActivity, usernames, Toast.LENGTH_LONG).show()\n" +
                "                }\n" +
                "            )\n" +
                "    }"
    }

    fun flatMapCode():String{
        return "private fun flatMap() {\n" +
                "        loader.visibility = View.VISIBLE\n" +
                "        val userDetails = StringBuilder()\n" +
                "        val subscribe = getUserListObservable()\n" +
                "            .flatMap {\n" +
                "                Observable.fromIterable(it)\n" +
                "            }\n" +
                "            .flatMap {\n" +
                "                return@flatMap getUserDetailObservable(it.id)\n" +
                "            }.subscribeOn(Schedulers.io())\n" +
                "            .observeOn(AndroidSchedulers.mainThread())\n" +
                "            .subscribe(\n" +
                "                {\n" +
                "                    userDetails.append(\"User Details : \${it.firstname}, \${it.lastname} \\n\")\n" +
                "                },\n" +
                "                { error ->\n" +
                "                    error.printStackTrace()\n" +
                "                    loader.visibility = View.GONE\n" +
                "                },\n" +
                "                {\n" +
                "                    loader.visibility = View.GONE\n" +
                "                    Toast.makeText(this@MainActivity, userDetails.toString(), Toast.LENGTH_LONG).show()\n" +
                "                }\n" +
                "            )\n" +
                "\n" +
                "    }"
    }


    fun flatMapWithZipCode():String{
        return " private fun flatMapWithZip() {\n" +
                "        loader.visibility = View.VISIBLE\n" +
                "        val usernames = StringBuilder()\n" +
                "        val subscribe = getUserListObservable()\n" +
                "            .flatMap {\n" +
                "                Observable.fromIterable(it)\n" +
                "            }\n" +
                "            .flatMap {\n" +
                "                return@flatMap Observable.zip(getUserDetailObservable(it.id), Observable.just(it),\n" +
                "                    BiFunction<UserDetail, User, Pair<UserDetail, User>> { userDetail, user ->\n" +
                "                        return@BiFunction Pair(userDetail, user)\n" +
                "                    })\n" +
                "            }.subscribeOn(Schedulers.io())\n" +
                "            .observeOn(AndroidSchedulers.mainThread())\n" +
                "            .subscribe(\n" +
                "                {\n" +
                "                    val userData = it.first\n" +
                "                    val user = it.second\n" +
                "                    usernames.append(\${userData.id} = = = \${user.firstname}\\n\")\n" +
                "                },\n" +
                "                {\n" +
                "                    it.printStackTrace()\n" +
                "                },\n" +
                "                {\n" +
                "                    loader.visibility = View.GONE\n" +
                "                    Toast.makeText(this@MainActivity, usernames.toString(), Toast.LENGTH_LONG).show()\n" +
                "                }\n" +
                "            )\n" +
                "    }"
    }
}