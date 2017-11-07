import { Schema, SchemaSettings } from '@orbit/data';
const schemaDefinition : SchemaSettings = {
    models: {
        jobsDbPosting: {
            attributes: {
                address: { type: 'string' },
                baseSalary: { type: 'string' },
                datePosted: { type: 'string' },
                description: { type: 'string' },
                hiringOrganization: { type: 'string' },
                jobLocation: { type: 'string' },
                name: { type: 'string' },
                title: { type: 'string' },
            },
        },
    }
};
export default new Schema(schemaDefinition);