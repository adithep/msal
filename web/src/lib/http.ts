import normalize from 'json-api-normalizer';

export const parse = (response) => {
    const parsedResponse = JSON.parse(response.text);
    return normalize(parsedResponse);
};