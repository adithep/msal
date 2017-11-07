import scrapy
from rx import Observable


class JobsDBItem(scrapy.Item):
    id = scrapy.Field()
    title = scrapy.Field()
    hiringOrganization = scrapy.Field()
    name = scrapy.Field()
    jobLocation = scrapy.Field()
    address = scrapy.Field()
    description = scrapy.Field()
#TODO: datePosted is currently incorrectly scrapped. Investigate.
    datePosted = scrapy.Field()
    baseSalary = scrapy.Field()


def each_prop(property):
    key = property.xpath('@itemprop').extract_first()
    val = property.xpath('string(.)').extract_first()
    obj = JobsDBItem()
    obj[key] = val
    return obj


def join_dict_with_single_key(a, b):
    b_key = next(iter(b))
    a[b_key] = '{0} {1}'.format(a[b_key], b[b_key]) if b_key in a else b[b_key]
    return a


class JobsDbSpider(scrapy.Spider):
    name = "jobsdbposts"
    start_urls = [
        'http://th.jobsdb.com/TH/EN/Search/FindJobs?KeyOpt=COMPLEX&JSRV=1&RLRSF=1&JobCat=1&JSSRC=HPSS',
    ]

    def process_item(self, item, spider):
        item.setdefault('id', str(ObjectId()))
        return item

    def parse(self, response):
        source = Observable.from_(response.xpath('//div[@itemscope]'))
        item = source.flat_map(lambda scope:
            Observable.from_(scope.xpath('.//*[@itemprop]'))
            .map(each_prop)
            .reduce(join_dict_with_single_key)
        )
        item.subscribe(self.logger.info)
        next_source = Observable.from_(response.css('a.pagebox-next'))
        next_stream = next_source.map(lambda x: response.follow(x, self.parse))
        final_stream = item.concat(next_stream)
        return final_stream.to_blocking()
