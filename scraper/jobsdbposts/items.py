from scrapy.item import Item, Field

class JobsDBItem(Item):
    id = Field()
    title = Field()
    hiringOrganization = Field()
    name = Field()
    jobLocation = Field()
    address = Field()
    description = Field()
    datePosted = Field()
    baseSalary = Field()