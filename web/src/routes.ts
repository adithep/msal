import { Component } from './interfaces';
import { Counter, counterLens } from './components/counter';
import { Jobs, jobsLens } from './components/jobs';
import { Speaker } from './components/speaker';

export interface RouteValue {
    component: Component;
    scope: any;
}
export interface Routes {
    readonly [index: string]: RouteValue;
}

export const routes: Routes = {
    '/': { component: Counter, scope: {onion: counterLens} },
    '/p2': { component: Jobs, scope: {onion: jobsLens} }
};

export const initialRoute = '/';
