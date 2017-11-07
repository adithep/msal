from scrapy.exporters import CsvItemExporter
from bson.objectid import ObjectId

class CsvPipeline(object):
    def __init__(self):
        self.file = open("/data/jobsdb.csv", 'wb')
        self.exporter = CsvItemExporter(self.file)
        self.exporter.start_exporting()
 
    def close_spider(self, spider):
        self.exporter.finish_exporting()
        self.file.close()
 
    def process_item(self, item, spider):
        item.setdefault('id', str(ObjectId()))
        self.exporter.export_item(item)
        return item