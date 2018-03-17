package tsuyogoro.sugorokuon.search

import dagger.Subcomponent

@Subcomponent(modules = [
    SearchModule::class
])
interface SearchSubComponent {

    fun inject(fragment: SearchFragment)

}