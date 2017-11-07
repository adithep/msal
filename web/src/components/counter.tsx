import xs, { Stream } from 'xstream';
import { VNode, DOMSource } from '@cycle/dom';
import { StateSource } from 'cycle-onionify';

import { BaseSources, BaseSinks } from '../interfaces';

import {State as GlobalState} from "./app";

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
    status: string;
}
export const defaultState: State = {
    count: 30,
    status: 'a',
};
export type Reducer = (prev: State) => State | undefined;

export const counterLens = { //    { val: 3, status: 'ready' }
    get: ( state : GlobalState ) => ({count: state.counter.count, status: state.status}),
    set: (state : GlobalState, childState : State) => ({...state, counter: {count: childState.count}, status: childState.status})
};

export function Counter({ DOM, onion }: Sources): Sinks {
    const action$: Stream<Reducer> = intent(DOM);
    const vdom$: Stream<VNode> = view(onion.state$);

    const routes$ = DOM.select('[data-action="navigate"]')
        .events('click')
        .mapTo('/p2');

    return {
        DOM: vdom$,
        onion: action$,
        router: routes$
    };
}

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
    return state$.map((a) =>
        <div>
            <h2>My Awesome Cycle.js app - Page 1</h2>
            <span>
                {'Counter: ' + a.count}
            </span>
            <span>
                {JSON.stringify(a)}
            </span>
            <button type="button" className="add mdc-button mdc-button--raised" data-mdc-auto-init="MDCRipple">
                Increase
            </button>
            <button type="button" className="subtract mdc-button mdc-button--raised">
                Decrease
            </button>
            <button
                type="button"
                className="mdc-button mdc-button--raised"
                data-action="navigate"
            >
                Page 2
            </button>
        </div>
    );
}
