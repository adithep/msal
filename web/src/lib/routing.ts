import { HistoryAction } from "cyclic-router";
import { is } from 'ramda';

export const mapLink = (ev: Event) : HistoryAction => {
    ev.preventDefault();
    const target = ev.target as HTMLAnchorElement;
    const link = target.getAttribute('href');
    return (is(String, link)) ? link : '/';
};