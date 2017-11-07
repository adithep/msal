import xs, { Stream } from 'xstream';
import { VNode, DOMSource } from '@cycle/dom';
import { StateSource } from 'cycle-onionify';
import build from 'redux-object';

import { BaseSources, BaseSinks } from '../interfaces';

import {State as GlobalState} from "./app";

import { parse } from "../lib/http"

// Types
export interface Sources extends BaseSources {
    onion: StateSource<State>;
}
export interface Sinks extends BaseSinks {
    onion?: Stream<Reducer>;
}

// State
export interface State {
    count: number;
    resources: any;
}
export const defaultState: State = {
    count: 30,
    resources: {},
};
export type Reducer = (prev: State) => State | undefined;

export const jobsLens = { //    { val: 3, status: 'ready' }
    get: ( state : GlobalState ) => ({count: state.counter.count, resources: state.resources}),
    set: (state : GlobalState, childState : State) => ({...state, counter: {count: childState.count}, resources: childState.resources})
};

export function Jobs({ DOM, onion, HTTP, orbit }: Sources): Sinks {

    const request$ = xs.of({
        url: 'http://localhost:9488/jobs-db-postings', // GET method by default
        category: 'jobsDBPosts',
    });

    const orbit$ = xs.of(q => q.findRecords('jobsDbPosting'));

    const response$ = HTTP
        .select('jobsDBPosts')
        .flatten();

    const action$: Stream<Reducer> = intent(DOM, response$);
    const vdom$: Stream<VNode> = view(onion.state$);

    return {
        DOM: vdom$,
        HTTP: request$,
        onion: action$,
        orbit: orbit$,
    };
}

function intent(DOM: DOMSource, response$: any): Stream<Reducer> {
    const init$ = xs.of<Reducer>(
        prevState => (prevState === undefined ? defaultState : prevState)
    );

    const add$: Stream<Reducer> = DOM.select('.add')
        .events('click')
        .mapTo<Reducer>(state => ({ ...state, count: state.count + 1 }));

    const subtract$: Stream<Reducer> = DOM.select('.subtract')
        .events('click')
        .mapTo<Reducer>(state => ({ ...state, count: state.count - 1 }));

    const resources$: Stream<Reducer> = response$
        .map(response => {
            return state => ({ ...state, resources: parse(response) })
        });

    return xs.merge(init$, add$, subtract$, resources$);
}

function view(state$: Stream<State>): Stream<VNode> {
    return state$
        .map(state => ({jobsDbPosting: build(state.resources, 'jobsDbPosting')}))
        .map(({ jobsDbPosting }) =>
            <div>
                { jobsDbPosting.map(row => <span>{JSON.stringify(row)}</span>) }
            </div>
    );
}
