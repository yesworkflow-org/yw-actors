package org.yesworkflow.actors;

enum ActorFSM {
    CONSTRUCTED,
    PROPERTIES_SET,
    ELABORATED,
    CONFIGURED,
    INITIALIZED,
    STARTED,
    STEPPED,
    WRAPPED_UP,
    DISPOSED
}