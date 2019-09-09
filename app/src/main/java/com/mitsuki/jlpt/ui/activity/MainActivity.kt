package com.mitsuki.jlpt.ui.activity

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.mitsuki.jlpt.R
import com.mitsuki.jlpt.ui.adapter.WordAdapter
import com.mitsuki.jlpt.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.kodein.di.generic.instance
import androidx.recyclerview.widget.ItemTouchHelper
import com.mitsuki.jlpt.app.*
import com.mitsuki.jlpt.app.hint.showOperationResult
import com.mitsuki.jlpt.app.hint.toastShort
import com.mitsuki.jlpt.app.kind.Kind
import com.mitsuki.jlpt.app.kind.getKind
import com.mitsuki.jlpt.app.resultmanager.OnResultManager
import com.mitsuki.jlpt.app.smoothscroll.SmoothScrollLayoutManager
import com.mitsuki.jlpt.app.tts.NativeTTS
import com.mitsuki.jlpt.app.tts.Speaker
import com.mitsuki.jlpt.base.BaseActivity
import com.mitsuki.jlpt.module.mainKodeinModule
import com.mitsuki.jlpt.ui.widget.SwipeDeleteEvent
import com.mitsuki.jlpt.viewmodel.MainEvent
import com.uber.autodispose.autoDisposable
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers

class MainActivity : BaseActivity<MainViewModel>() {

    private val WORD_KIND = "WORD_KIND"

    override val kodeinModule = mainKodeinModule

    override val viewModel: MainViewModel by instance()
    private val mAdapter: WordAdapter by instance()
    private val itemTouchHelper: ItemTouchHelper by instance()
    private val swipeDeleteEvent: SwipeDeleteEvent by instance()

    private val speaker: Speaker by lazy { NativeTTS.createSpeaker(this) }

    override fun initView(savedInstanceState: Bundle?) = R.layout.activity_main

    override fun initData(savedInstanceState: Bundle?) {
        initToolbar()
        initComponent()

        switchMode(getInt(WORD_KIND))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.nav_all -> switchMode(Kind.ALL)
            R.id.nav_n1 -> switchMode(Kind.N1)
            R.id.nav_n2 -> switchMode(Kind.N2)
            R.id.nav_n3 -> switchMode(Kind.N3)
            R.id.nav_n4 -> switchMode(Kind.N4)
            R.id.nav_n5 -> switchMode(Kind.N5)
            R.id.nav_numeral -> switchMode(Kind.NUMERAL)
            R.id.nav_invisible -> switchMode(Kind.INVISIBLE)
            R.id.nav_test -> switchMode(Kind.MEMORIES)
        }
        return false
    }

    private fun initToolbar() {
        setSupportActionBar(toolbar)
    }

    @SuppressLint("CheckResult")
    private fun initComponent() {
        itemTouchHelper.attachToRecyclerView(wordList)
        wordList.layoutManager = SmoothScrollLayoutManager(this)
        wordList.adapter = mAdapter

        swipeDeleteEvent.onSwipe.observeOn(Schedulers.io()).autoDisposable(scopeProvider)
            .subscribe { viewModel.changeWordState(it, mAdapter.getItemForOut(it)) }

        mAdapter.parentSubject.autoDisposable(scopeProvider)
            .subscribe { speaker.speak(it.cn, it.kana) { toastShort { it } } }

        viewModel.observeData().autoDisposable(scopeProvider)
            .subscribe { mAdapter.submitList(it) { viewModel.checkListStatus() } }

        viewModel.observeEvent().autoDisposable(scopeProvider)
            .subscribe(this::onViewModelEvent)
    }

    private fun onViewModelEvent(event: MainEvent) {
        when (event) {
            MainEvent.SHOW_SNACKBAR -> showSnackbar()
            MainEvent.SCROLL_TO_TOP -> scrollToTop()
        }
    }

    private fun showSnackbar() = wordList.showOperationResult("操作成功", "撤销") {
        Completable.fromAction {}.observeOn(Schedulers.io()).autoDisposable(scopeProvider)
            .subscribe { viewModel.undoOperation() }
    }

    private fun scrollToTop() = wordList.smoothScrollToPosition(0)

    private fun switchMode(order: Int) {
        getKind(order)?.let {
            if (it.getMode() >= 0) {
                putInt(WORD_KIND, it.getMode())
                title = it.getTitle()
                mAdapter.setListMode(it.getMode() != Kind.INVISIBLE)
            }
            viewModel.switchMode(it.getMode())
        }
    }

}
