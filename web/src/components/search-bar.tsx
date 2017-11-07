import xs, { Stream } from 'xstream';
import { VNode, DOMSource } from '@cycle/dom';
import { StateSource } from 'cycle-onionify';
import { mapLink } from '../lib/routing';

import { BaseSources, BaseSinks } from '../interfaces';
import { HistoryAction } from "cyclic-router";

// Types
export interface Sources extends BaseSources {
    onion: StateSource<State>;
}
export interface Sinks extends BaseSinks {
    onion: Stream<Reducer>;
}

// State
export interface State {
    count: number;
}
export const defaultState: State = {
    count: 30
};
export type Reducer = (prev: State) => State | undefined;

export function SearchBar({ DOM, onion }: Sources): Sinks {
    const action$: Stream<Reducer> = intent(DOM);
    const vdom$: Stream<VNode> = view(onion.state$);
    const routes$ = routes(DOM);

    return {
        DOM: vdom$,
        onion: action$,
        router: routes$
    };
}

const routes = (DOM: DOMSource): Stream<HistoryAction> => {
    const p2$ = DOM.select('a')
        .events('click')
        .map(mapLink);

    return xs.merge(p2$);
};

function intent(DOM: DOMSource): Stream<Reducer> {
    const init$ = xs.of<Reducer>(
        prevState => (prevState === undefined ? defaultState : prevState)
    );

    const add$: Stream<Reducer> = DOM.select('.add')
        .events('click')
        .mapTo<Reducer>(state => ({ ...state, count: state.count + 1 }));

    const subtract$: Stream<Reducer> = DOM.select('.subtract')
        .events('click')
        .mapTo<Reducer>(state => ({ ...state, count: state.count - 1 }));

    return xs.merge(init$, add$, subtract$);
}

function view(state$: Stream<State>): Stream<VNode> {
    return state$.map(({ count }) =>
        <header className="mdc-toolbar">
            <div className="mdc-toolbar__row">
                <section className="mdc-toolbar__section mdc-toolbar__section--align-start">
                    <a href="/" className="material-icons mdc-toolbar__icon--menu">menu</a>
                    <a href="/p2" className="mdc-toolbar__title">Title</a>
                </section>
            </div>
        </header>
    );
}
