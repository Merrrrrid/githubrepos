package com.klymchuk.githubrepos.navigation

class TransactionRouter : CoreRouter {

    val commands = mutableListOf<Navigation.Command>()

    override fun add(destination: Destination, modifier: Navigation.Modifier?) {
        commands.add(Navigation.Command.Add(destination, modifier))
    }

    override fun replace(destination: Destination, modifier: Navigation.Modifier?) {
        commands.add(Navigation.Command.Replace(destination, modifier))
    }

    override fun back() {
        commands.add(Navigation.Command.Back())
    }

    override fun backTo(destination: Destination) {
        commands.add(Navigation.Command.Back(destination))
    }

    override fun newRoot(destination: Destination, modifier: Navigation.Modifier?) {
        commands.add(Navigation.Command.NewRoot(destination, modifier))
    }
}