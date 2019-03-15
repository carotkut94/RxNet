package com.death.rxnet

import androidx.appcompat.widget.SearchView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

object RxSearchViewObservable{

    fun fromView(searchView:SearchView): Observable<String> {

        val subject:PublishSubject<String> = PublishSubject.create()
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                subject.onNext(query)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                subject.onNext(newText)
                return true
            }

        })

        return subject
    }
}