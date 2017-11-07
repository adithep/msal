BOT_NAME = 'jobsdbposts'

SPIDER_MODULES = ['jobsdbposts.spiders']
NEWSPIDER_MODULE = 'jobsdbposts.spiders'

ITEM_PIPELINES = {
    'jobsdbposts.pipelines.CsvPipeline': 300,
    #'template.pipelines.RedisPipeline': 301,
}

LOG_LEVEL = 'INFO'