package com.klymchuk.githubrepos.navigation

interface CoreRouter {
    fun add(destination: Destination, modifier: Navigation.Modifier? = null)
    fun replace(destination: Destination, modifier: Navigation.Modifier? = null)
    fun back()
    fun backTo(destination: Destination)
    fun newRoot(destination: Destination, modifier: Navigation.Modifier? = null)
}

interface Router : CoreRouter {
    fun runTogether(action: (router: CoreRouter) -> Unit)
}

class RouterImpl(private val mNavigator: Navigator) : Router {
    override fun add(destination: Destination, modifier: Navigation.Modifier?) {
        mNavigator.enqueue(Navigation.Command.Add(destination, modifier))
    }

    override fun replace(destination: Destination, modifier: Navigation.Modifier?) {
        mNavigator.enqueue(Navigation.Command.Replace(destination, modifier))
    }

    override fun back() {
        mNavigator.enqueue(Navigation.Command.Back())
    }

    override fun backTo(destination: Destination) {
        mNavigator.enqueue(Navigation.Command.Back(destination))
    }

    override fun newRoot(destination: Destination, modifier: Navigation.Modifier?) {
        mNavigator.enqueue(Navigation.Command.NewRoot(destination, modifier))
    }

    override fun runTogether(action: (router: CoreRouter) -> Unit) {
        val router = TransactionRouter()
        action.invoke(router)

        mNavigator.enqueue(Navigation.Command.Transaction(router.commands))
    }
}