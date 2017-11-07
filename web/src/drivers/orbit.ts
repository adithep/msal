import xs, { Stream } from 'xstream';
import {adapt} from '@cycle/run/lib/adapt';
import Store from '@orbit/store';
import schema  from '../schema/jobs-db-posts'
import Coordinator, { SyncStrategy, RequestStrategy } from '@orbit/coordinator';
import JSONAPISource from '@orbit/jsonapi';
import Orbit from '@orbit/data';
Orbit.fetch = window.fetch.bind(window);

export default function orbit() {
    const store = new Store({ schema });
    const remote = new JSONAPISource({
        schema,
        name: 'remote',
        host: 'http://localhost:9488',
        defaultFetchHeaders: {
            Accept: "*/*",
        }
    });
    const coordinator = new Coordinator({
        sources: [store, remote]
    });

    coordinator.addStrategy(new RequestStrategy({
        source: 'store',
        on: 'beforeQuery',
        target: 'remote',
        action: 'pull',
        blocking: false
    }));
// Update the remote server whenever the store is updated
    coordinator.addStrategy(new RequestStrategy({
        source: 'store',
        on: 'beforeUpdate',
        target: 'remote',
        action: 'push',
        blocking: false
    }));
// Sync all changes received from the remote server to the store
    coordinator.addStrategy(new SyncStrategy({
        source: 'remote',
        target: 'store',
        blocking: false
    }));

    coordinator.activate();

    function orbitDriver(outgoing$) {
        const incoming$ = outgoing$.addListener({
            next: outgoing => {
                store.query(outgoing)
                    .then(a => console.log(a));
            },
            error: () => {},
            complete: () => {},
        });

        // const incoming$ = xs.create({
        //     start: listener => {
        //         sock.onReceive(function (msg) {
        //             listener.next(msg);
        //         });
        //     },
        //     stop: () => {},
        // });

        return adapt(incoming$);
    }

    return orbitDriver;
}