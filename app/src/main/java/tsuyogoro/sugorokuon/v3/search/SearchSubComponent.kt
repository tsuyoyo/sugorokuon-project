package tsuyogoro.sugorokuon.v3.search

import dagger.Subcomponent

@Subcomponent(modules = [
    SearchModule::class
])
interface SearchSubComponent {

    fun inject(fragment: SearchFragment)

}