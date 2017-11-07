import scrapy
import json
from functools import reduce


class JobsDBItem(scrapy.Item):
    id = scrapy.Field()
    title = scrapy.Field()
    hiringOrganization = scrapy.Field()
    name = scrapy.Field()
    jobLocation = scrapy.Field()
    address = scrapy.Field()
    description = scrapy.Field()
    datePosted = scrapy.Field()
    baseSalary = scrapy.Field()


def each_prop(property):
    key = property.xpath('@itemprop').extract_first()
    val = property.xpath('string(.)').extract_first()
    obj = JobsDBItem()
    obj[key] = val
    print("{0}: {1}".format(key, val))
    return obj


def join_dict_with_single_key(a, b):
    b_key = next(iter(b))
    a[b_key] = '{0} {1}'.format(a[b_key], b[b_key]) if b_key in a else b[b_key]
    return a


def each_scope(scope):
    print("current scope:", scope.xpath('@itemtype').extract())
    arr = map(each_prop, scope.xpath('.//*[@itemprop]'))
    nobj = reduce(join_dict_with_single_key, arr)
    yield nobj


class JobsDbSpider(scrapy.Spider):
    name = "jobsdbposts"
    start_urls = [
        'http://th.jobsdb.com/TH/EN/Search/FindJobs?KeyOpt=COMPLEX&JSRV=1&RLRSF=1&JobCat=1&JSSRC=HPSS',
    ]

    def parse(self, response):
        map(each_scope, response.xpath('//div[@itemscope]'))
        map(lambda next_page: response.follow(next_page, self.parse),
            response.css('a.pagebox-next'))

