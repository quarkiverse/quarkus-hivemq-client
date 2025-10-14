---
description: Email development, content management, and communication patterns
alwaysApply: true
---

# Email & Content Management Patterns

Consolidated from: email-development-guidelines.mdc, content-management-workflow.mdc

## Email Development with React Email

### Basic Email Template Structure
```tsx
// emails/base-template.tsx
import {
  Body,
  Container,
  Head,
  Heading,
  Html,
  Img,
  Link,
  Preview,
  Section,
  Text,
  Button,
  Row,
  Column,
} from '@react-email/components';

interface BaseEmailProps {
  previewText: string;
  children: React.ReactNode;
}

export function BaseEmail({ previewText, children }: BaseEmailProps) {
  return (
    <Html>
      <Head>
        <style>{`
          @font-face {
            font-family: 'Inter';
            font-style: normal;
            font-weight: 400;
            src: url('https://fonts.gstatic.com/s/inter/v12/UcCO3FwrK3iLTeHuS_fvQtMwCp50KnMw2boKoduKmMEVuLyfAZ9hiA.woff2') format('woff2');
          }
        `}</style>
      </Head>
      <Preview>{previewText}</Preview>
      <Body style={main}>
        <Container style={container}>
          <Section style={header}>
            <Img
              src="https://example.com/logo.png"
              width="150"
              height="50"
              alt="Logo"
              style={logo}
            />
          </Section>
          
          {children}
          
          <Section style={footer}>
            <Text style={footerText}>
              © {new Date().getFullYear()} Your Company. All rights reserved.
            </Text>
            <Row>
              <Column align="center">
                <Link href="https://example.com/unsubscribe" style={footerLink}>
                  Unsubscribe
                </Link>
                {' • '}
                <Link href="https://example.com/privacy" style={footerLink}>
                  Privacy Policy
                </Link>
              </Column>
            </Row>
          </Section>
        </Container>
      </Body>
    </Html>
  );
}

// Styles
const main = {
  backgroundColor: '#f6f9fc',
  fontFamily: 'Inter, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif',
};

const container = {
  margin: '0 auto',
  padding: '20px 0 48px',
  width: '560px',
};

const header = {
  padding: '24px 0',
  textAlign: 'center' as const,
};

const logo = {
  margin: '0 auto',
};

const footer = {
  padding: '24px 0',
  textAlign: 'center' as const,
};

const footerText = {
  color: '#8898aa',
  fontSize: '12px',
  lineHeight: '16px',
  margin: '0',
};

const footerLink = {
  color: '#8898aa',
  fontSize: '12px',
  textDecoration: 'underline',
};
```

### Transactional Email Templates
```tsx
// emails/order-confirmation.tsx
import { BaseEmail } from './base-template';
import {
  Section,
  Text,
  Button,
  Row,
  Column,
  Hr,
} from '@react-email/components';

interface OrderConfirmationProps {
  customerName: string;
  orderNumber: string;
  orderDate: string;
  items: Array<{
    name: string;
    quantity: number;
    price: number;
  }>;
  subtotal: number;
  shipping: number;
  tax: number;
  total: number;
  shippingAddress: {
    street: string;
    city: string;
    state: string;
    zip: string;
  };
}

export default function OrderConfirmation({
  customerName,
  orderNumber,
  orderDate,
  items,
  subtotal,
  shipping,
  tax,
  total,
  shippingAddress,
}: OrderConfirmationProps) {
  return (
    <BaseEmail previewText={`Order #${orderNumber} confirmed`}>
      <Section style={content}>
        <Heading style={heading}>Order Confirmed!</Heading>
        <Text style={paragraph}>
          Hi {customerName},
        </Text>
        <Text style={paragraph}>
          Thank you for your order. We've received your order and will begin
          processing it right away.
        </Text>
        
        <Section style={orderInfo}>
          <Row>
            <Column>
              <Text style={label}>Order Number</Text>
              <Text style={value}>{orderNumber}</Text>
            </Column>
            <Column>
              <Text style={label}>Order Date</Text>
              <Text style={value}>{orderDate}</Text>
            </Column>
          </Row>
        </Section>
        
        <Section style={itemsSection}>
          <Text style={sectionTitle}>Order Items</Text>
          {items.map((item, index) => (
            <Row key={index} style={itemRow}>
              <Column style={itemName}>
                <Text>{item.name}</Text>
              </Column>
              <Column style={itemQuantity}>
                <Text>×{item.quantity}</Text>
              </Column>
              <Column style={itemPrice}>
                <Text>${(item.price * item.quantity).toFixed(2)}</Text>
              </Column>
            </Row>
          ))}
          
          <Hr style={divider} />
          
          <Row style={summaryRow}>
            <Column><Text>Subtotal</Text></Column>
            <Column align="right">
              <Text>${subtotal.toFixed(2)}</Text>
            </Column>
          </Row>
          <Row style={summaryRow}>
            <Column><Text>Shipping</Text></Column>
            <Column align="right">
              <Text>${shipping.toFixed(2)}</Text>
            </Column>
          </Row>
          <Row style={summaryRow}>
            <Column><Text>Tax</Text></Column>
            <Column align="right">
              <Text>${tax.toFixed(2)}</Text>
            </Column>
          </Row>
          <Row style={totalRow}>
            <Column><Text style={totalLabel}>Total</Text></Column>
            <Column align="right">
              <Text style={totalValue}>${total.toFixed(2)}</Text>
            </Column>
          </Row>
        </Section>
        
        <Section style={addressSection}>
          <Text style={sectionTitle}>Shipping Address</Text>
          <Text style={address}>
            {shippingAddress.street}<br />
            {shippingAddress.city}, {shippingAddress.state} {shippingAddress.zip}
          </Text>
        </Section>
        
        <Button href="https://example.com/orders" style={button}>
          View Order Details
        </Button>
      </Section>
    </BaseEmail>
  );
}

// Styles
const content = {
  padding: '0 20px',
};

const heading = {
  fontSize: '32px',
  fontWeight: '700',
  textAlign: 'center' as const,
  margin: '30px 0',
};

const paragraph = {
  fontSize: '16px',
  lineHeight: '24px',
  margin: '16px 0',
};

const orderInfo = {
  backgroundColor: '#f7f7f7',
  borderRadius: '8px',
  padding: '20px',
  margin: '20px 0',
};

const label = {
  fontSize: '12px',
  fontWeight: '600',
  textTransform: 'uppercase' as const,
  color: '#666',
  margin: '0',
};

const value = {
  fontSize: '16px',
  fontWeight: '600',
  margin: '4px 0 0 0',
};

const button = {
  backgroundColor: '#5469d4',
  borderRadius: '8px',
  color: '#fff',
  fontSize: '16px',
  fontWeight: '600',
  textDecoration: 'none',
  textAlign: 'center' as const,
  display: 'block',
  padding: '12px 20px',
  margin: '30px auto',
};
```

### Marketing Email Templates
```tsx
// emails/newsletter.tsx
import { BaseEmail } from './base-template';
import {
  Section,
  Text,
  Button,
  Img,
  Link,
  Row,
  Column,
} from '@react-email/components';

interface NewsletterProps {
  subscriberName: string;
  articles: Array<{
    title: string;
    excerpt: string;
    image: string;
    link: string;
    author: string;
    readTime: string;
  }>;
  featuredArticle: {
    title: string;
    excerpt: string;
    image: string;
    link: string;
  };
}

export default function Newsletter({
  subscriberName,
  articles,
  featuredArticle,
}: NewsletterProps) {
  return (
    <BaseEmail previewText="Your weekly digest is here">
      <Section style={hero}>
        <Heading style={heroHeading}>Weekly Digest</Heading>
        <Text style={heroText}>
          Hi {subscriberName}, here's what's new this week
        </Text>
      </Section>
      
      {/* Featured Article */}
      <Section style={featured}>
        <Img
          src={featuredArticle.image}
          width="520"
          height="280"
          alt={featuredArticle.title}
          style={featuredImage}
        />
        <Text style={featuredTitle}>{featuredArticle.title}</Text>
        <Text style={featuredExcerpt}>{featuredArticle.excerpt}</Text>
        <Button href={featuredArticle.link} style={featuredButton}>
          Read More
        </Button>
      </Section>
      
      {/* Article List */}
      <Section style={articleList}>
        {articles.map((article, index) => (
          <Row key={index} style={articleRow}>
            <Column style={articleImageCol}>
              <Img
                src={article.image}
                width="160"
                height="100"
                alt={article.title}
                style={articleImage}
              />
            </Column>
            <Column style={articleContentCol}>
              <Link href={article.link} style={articleTitle}>
                {article.title}
              </Link>
              <Text style={articleExcerpt}>{article.excerpt}</Text>
              <Text style={articleMeta}>
                By {article.author} • {article.readTime} read
              </Text>
            </Column>
          </Row>
        ))}
      </Section>
      
      {/* CTA Section */}
      <Section style={ctaSection}>
        <Text style={ctaText}>
          Want to contribute? We're always looking for great content.
        </Text>
        <Button href="https://example.com/write" style={ctaButton}>
          Start Writing
        </Button>
      </Section>
    </BaseEmail>
  );
}

// Styles
const hero = {
  backgroundColor: '#5469d4',
  padding: '40px 20px',
  textAlign: 'center' as const,
};

const heroHeading = {
  color: '#fff',
  fontSize: '36px',
  fontWeight: '700',
  margin: '0 0 10px',
};

const heroText = {
  color: '#fff',
  fontSize: '18px',
  margin: '0',
};

const featured = {
  padding: '40px 20px',
  textAlign: 'center' as const,
};

const featuredImage = {
  borderRadius: '8px',
  marginBottom: '20px',
};

const featuredTitle = {
  fontSize: '24px',
  fontWeight: '700',
  margin: '0 0 10px',
};

const articleRow = {
  borderBottom: '1px solid #eee',
  padding: '20px 0',
};

const articleImage = {
  borderRadius: '4px',
};

const articleTitle = {
  fontSize: '18px',
  fontWeight: '600',
  color: '#111',
  textDecoration: 'none',
};
```

### Email Sending Implementation
```typescript
// lib/email.ts
import { Resend } from 'resend';
import { render } from '@react-email/render';

const resend = new Resend(process.env.RESEND_API_KEY);

interface SendEmailOptions {
  to: string | string[];
  subject: string;
  react: React.ReactElement;
  from?: string;
  replyTo?: string;
  cc?: string | string[];
  bcc?: string | string[];
  attachments?: Array<{
    filename: string;
    content: Buffer | string;
  }>;
  tags?: Array<{
    name: string;
    value: string;
  }>;
  scheduledAt?: string;
}

export async function sendEmail({
  to,
  subject,
  react,
  from = 'noreply@example.com',
  replyTo,
  cc,
  bcc,
  attachments,
  tags,
  scheduledAt,
}: SendEmailOptions) {
  try {
    const html = render(react);
    const text = render(react, { plainText: true });

    const data = await resend.emails.send({
      from,
      to: Array.isArray(to) ? to : [to],
      subject,
      html,
      text,
      reply_to: replyTo,
      cc,
      bcc,
      attachments,
      tags,
      scheduled_at: scheduledAt,
    });

    // Log email sent
    await db.emailLog.create({
      data: {
        messageId: data.id,
        to: Array.isArray(to) ? to.join(', ') : to,
        subject,
        status: 'sent',
        sentAt: new Date(),
      },
    });

    return { success: true, data };
  } catch (error) {
    // Log email failure
    await db.emailLog.create({
      data: {
        to: Array.isArray(to) ? to.join(', ') : to,
        subject,
        status: 'failed',
        error: error.message,
      },
    });

    throw error;
  }
}

// Batch email sending
export async function sendBatchEmails(
  emails: Array<Omit<SendEmailOptions, 'from'>>
) {
  const results = await Promise.allSettled(
    emails.map(email => sendEmail(email))
  );

  const successful = results.filter(r => r.status === 'fulfilled').length;
  const failed = results.filter(r => r.status === 'rejected').length;

  return { successful, failed, total: emails.length };
}

// Email preview endpoint
export async function previewEmail(
  template: string,
  props: Record<string, any>
) {
  const templates = {
    'order-confirmation': OrderConfirmation,
    'newsletter': Newsletter,
    'welcome': WelcomeEmail,
    // Add more templates
  };

  const Template = templates[template];
  if (!Template) {
    throw new Error(`Template ${template} not found`);
  }

  const html = render(<Template {...props} />);
  return html;
}
```

## Content Management Patterns

### Content Modeling
```typescript
// types/content.ts
interface ContentBase {
  id: string;
  title: string;
  slug: string;
  status: 'draft' | 'published' | 'archived';
  author: User;
  createdAt: Date;
  updatedAt: Date;
  publishedAt?: Date;
  metadata: {
    description?: string;
    keywords?: string[];
    ogImage?: string;
  };
}

interface BlogPost extends ContentBase {
  type: 'blog';
  content: string; // Markdown or rich text
  excerpt: string;
  coverImage?: string;
  categories: Category[];
  tags: Tag[];
  readingTime: number;
}

interface Page extends ContentBase {
  type: 'page';
  content: string;
  template: 'default' | 'landing' | 'contact';
  components?: Array<{
    type: string;
    props: Record<string, any>;
  }>;
}

interface Product extends ContentBase {
  type: 'product';
  description: string;
  price: number;
  images: string[];
  variants: ProductVariant[];
  inventory: number;
}

type Content = BlogPost | Page | Product;
```

### Content API
```typescript
// lib/content.ts
export class ContentAPI {
  async getContent<T extends Content>(
    type: T['type'],
    slug: string
  ): Promise<T | null> {
    const content = await db.content.findFirst({
      where: {
        type,
        slug,
        status: 'published',
      },
      include: {
        author: true,
        categories: true,
        tags: true,
      },
    });

    return content as T;
  }

  async listContent<T extends Content>(
    type: T['type'],
    options: {
      page?: number;
      limit?: number;
      category?: string;
      tag?: string;
      author?: string;
      search?: string;
      sort?: 'latest' | 'popular' | 'alphabetical';
    } = {}
  ): Promise<{
    items: T[];
    total: number;
    page: number;
    totalPages: number;
  }> {
    const {
      page = 1,
      limit = 10,
      category,
      tag,
      author,
      search,
      sort = 'latest',
    } = options;

    const where = {
      type,
      status: 'published',
      ...(category && {
        categories: {
          some: { slug: category },
        },
      }),
      ...(tag && {
        tags: {
          some: { slug: tag },
        },
      }),
      ...(author && {
        author: { slug: author },
      }),
      ...(search && {
        OR: [
          { title: { contains: search, mode: 'insensitive' } },
          { content: { contains: search, mode: 'insensitive' } },
        ],
      }),
    };

    const orderBy = {
      latest: { publishedAt: 'desc' },
      popular: { viewCount: 'desc' },
      alphabetical: { title: 'asc' },
    }[sort];

    const [items, total] = await Promise.all([
      db.content.findMany({
        where,
        orderBy,
        skip: (page - 1) * limit,
        take: limit,
        include: {
          author: true,
          categories: true,
          tags: true,
        },
      }),
      db.content.count({ where }),
    ]);

    return {
      items: items as T[],
      total,
      page,
      totalPages: Math.ceil(total / limit),
    };
  }

  async createContent<T extends Content>(
    data: Omit<T, 'id' | 'createdAt' | 'updatedAt'>
  ): Promise<T> {
    const slug = data.slug || this.generateSlug(data.title);

    const content = await db.content.create({
      data: {
        ...data,
        slug,
      },
    });

    return content as T;
  }

  async updateContent<T extends Content>(
    id: string,
    data: Partial<T>
  ): Promise<T> {
    const content = await db.content.update({
      where: { id },
      data: {
        ...data,
        updatedAt: new Date(),
      },
    });

    // Clear cache
    await this.clearCache(content.type, content.slug);

    return content as T;
  }

  async publishContent(id: string): Promise<void> {
    await db.content.update({
      where: { id },
      data: {
        status: 'published',
        publishedAt: new Date(),
      },
    });
  }

  private generateSlug(title: string): string {
    return title
      .toLowerCase()
      .replace(/[^a-z0-9]+/g, '-')
      .replace(/^-+|-+$/g, '');
  }

  private async clearCache(type: string, slug: string): Promise<void> {
    // Clear specific content cache
    revalidatePath(`/${type}/${slug}`);
    // Clear listing pages
    revalidatePath(`/${type}`);
  }
}

export const contentAPI = new ContentAPI();
```

### Content Editor Component
```tsx
// components/ContentEditor.tsx
import { useState } from 'react';
import dynamic from 'next/dynamic';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';

const MDXEditor = dynamic(
  () => import('@mdxeditor/editor').then(mod => mod.MDXEditor),
  { ssr: false }
);

const contentSchema = z.object({
  title: z.string().min(1, 'Title is required'),
  slug: z.string().optional(),
  excerpt: z.string().max(200),
  content: z.string().min(1, 'Content is required'),
  status: z.enum(['draft', 'published']),
  categories: z.array(z.string()),
  tags: z.array(z.string()),
  metadata: z.object({
    description: z.string().optional(),
    keywords: z.array(z.string()).optional(),
  }),
});

type ContentFormData = z.infer<typeof contentSchema>;

export function ContentEditor({
  initialData,
  onSave,
}: {
  initialData?: Partial<ContentFormData>;
  onSave: (data: ContentFormData) => Promise<void>;
}) {
  const [content, setContent] = useState(initialData?.content || '');
  const [saving, setSaving] = useState(false);

  const form = useForm<ContentFormData>({
    resolver: zodResolver(contentSchema),
    defaultValues: {
      status: 'draft',
      categories: [],
      tags: [],
      ...initialData,
    },
  });

  const handleSave = async (data: ContentFormData) => {
    setSaving(true);
    try {
      await onSave({ ...data, content });
      toast.success('Content saved successfully');
    } catch (error) {
      toast.error('Failed to save content');
    } finally {
      setSaving(false);
    }
  };

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(handleSave)} className="space-y-6">
        <div className="grid grid-cols-2 gap-4">
          <FormField
            control={form.control}
            name="title"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Title</FormLabel>
                <FormControl>
                  <Input {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="slug"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Slug</FormLabel>
                <FormControl>
                  <Input {...field} placeholder="auto-generated" />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>

        <FormField
          control={form.control}
          name="excerpt"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Excerpt</FormLabel>
              <FormControl>
                <Textarea {...field} rows={3} />
              </FormControl>
              <FormDescription>
                Brief description (max 200 characters)
              </FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        <div className="space-y-2">
          <Label>Content</Label>
          <div className="border rounded-lg">
            <MDXEditor
              markdown={content}
              onChange={setContent}
              plugins={[
                headingsPlugin(),
                listsPlugin(),
                quotePlugin(),
                linkPlugin(),
                imagePlugin(),
                tablePlugin(),
                codeBlockPlugin(),
                markdownShortcutPlugin(),
              ]}
            />
          </div>
        </div>

        <div className="grid grid-cols-2 gap-4">
          <FormField
            control={form.control}
            name="categories"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Categories</FormLabel>
                <FormControl>
                  <MultiSelect
                    options={categoryOptions}
                    value={field.value}
                    onChange={field.onChange}
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />

          <FormField
            control={form.control}
            name="tags"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Tags</FormLabel>
                <FormControl>
                  <TagInput
                    value={field.value}
                    onChange={field.onChange}
                  />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>

        <Card>
          <CardHeader>
            <CardTitle>SEO Settings</CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            <FormField
              control={form.control}
              name="metadata.description"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Meta Description</FormLabel>
                  <FormControl>
                    <Textarea {...field} rows={2} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <FormField
              control={form.control}
              name="metadata.keywords"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Keywords</FormLabel>
                  <FormControl>
                    <TagInput
                      value={field.value || []}
                      onChange={field.onChange}
                      placeholder="Add keywords..."
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
          </CardContent>
        </Card>

        <div className="flex justify-between">
          <FormField
            control={form.control}
            name="status"
            render={({ field }) => (
              <FormItem>
                <Select
                  onValueChange={field.onChange}
                  defaultValue={field.value}
                >
                  <FormControl>
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                  </FormControl>
                  <SelectContent>
                    <SelectItem value="draft">Draft</SelectItem>
                    <SelectItem value="published">Published</SelectItem>
                  </SelectContent>
                </Select>
                <FormMessage />
              </FormItem>
            )}
          />

          <div className="space-x-2">
            <Button type="button" variant="outline">
              Preview
            </Button>
            <Button type="submit" disabled={saving}>
              {saving ? 'Saving...' : 'Save'}
            </Button>
          </div>
        </div>
      </form>
    </Form>
  );
}
```

### Content Workflow
```typescript
// lib/content-workflow.ts
export class ContentWorkflow {
  async submitForReview(contentId: string, authorId: string): Promise<void> {
    const content = await db.content.findUnique({
      where: { id: contentId },
    });

    if (!content) {
      throw new Error('Content not found');
    }

    if (content.authorId !== authorId) {
      throw new Error('Only the author can submit for review');
    }

    await db.$transaction([
      // Update content status
      db.content.update({
        where: { id: contentId },
        data: { status: 'pending_review' },
      }),
      // Create workflow entry
      db.contentWorkflow.create({
        data: {
          contentId,
          action: 'submitted_for_review',
          userId: authorId,
        },
      }),
      // Notify reviewers
      this.notifyReviewers(content),
    ]);
  }

  async approveContent(
    contentId: string,
    reviewerId: string,
    comments?: string
  ): Promise<void> {
    const reviewer = await this.validateReviewer(reviewerId);

    await db.$transaction([
      // Update content
      db.content.update({
        where: { id: contentId },
        data: {
          status: 'approved',
          reviewedBy: reviewerId,
          reviewedAt: new Date(),
        },
      }),
      // Log workflow
      db.contentWorkflow.create({
        data: {
          contentId,
          action: 'approved',
          userId: reviewerId,
          comments,
        },
      }),
      // Notify author
      this.notifyAuthor(contentId, 'approved', comments),
    ]);
  }

  async requestChanges(
    contentId: string,
    reviewerId: string,
    changes: string[]
  ): Promise<void> {
    const reviewer = await this.validateReviewer(reviewerId);

    await db.$transaction([
      // Update content
      db.content.update({
        where: { id: contentId },
        data: {
          status: 'changes_requested',
          reviewedBy: reviewerId,
          reviewedAt: new Date(),
        },
      }),
      // Create change requests
      db.changeRequest.createMany({
        data: changes.map(change => ({
          contentId,
          requestedBy: reviewerId,
          description: change,
          status: 'pending',
        })),
      }),
      // Notify author
      this.notifyAuthor(contentId, 'changes_requested', changes.join('\n')),
    ]);
  }

  async publishContent(
    contentId: string,
    publisherId: string,
    scheduledAt?: Date
  ): Promise<void> {
    const publisher = await this.validatePublisher(publisherId);

    if (scheduledAt && scheduledAt > new Date()) {
      // Schedule for future
      await db.scheduledPublication.create({
        data: {
          contentId,
          scheduledBy: publisherId,
          scheduledFor: scheduledAt,
        },
      });
    } else {
      // Publish immediately
      await db.$transaction([
        db.content.update({
          where: { id: contentId },
          data: {
            status: 'published',
            publishedAt: new Date(),
            publishedBy: publisherId,
          },
        }),
        db.contentWorkflow.create({
          data: {
            contentId,
            action: 'published',
            userId: publisherId,
          },
        }),
      ]);

      // Clear cache
      await this.clearContentCache(contentId);
    }
  }

  private async validateReviewer(userId: string): Promise<User> {
    const user = await db.user.findUnique({
      where: { id: userId },
    });

    if (!user || !['editor', 'admin'].includes(user.role)) {
      throw new Error('User is not authorized to review content');
    }

    return user;
  }

  private async notifyReviewers(content: Content): Promise<void> {
    const reviewers = await db.user.findMany({
      where: {
        role: { in: ['editor', 'admin'] },
        notifications: {
          emailOnReview: true,
        },
      },
    });

    await sendBatchEmails(
      reviewers.map(reviewer => ({
        to: reviewer.email,
        subject: 'New content for review',
        react: <ReviewRequestEmail content={content} reviewer={reviewer} />,
      }))
    );
  }
}
```

## Best Practices

### Email Development
- [ ] Use React Email for type-safe templates
- [ ] Test emails across clients
- [ ] Include plain text versions
- [ ] Optimize images for email
- [ ] Use web-safe fonts
- [ ] Keep width under 600px
- [ ] Test dark mode rendering
- [ ] Include unsubscribe links
- [ ] Use semantic HTML
- [ ] Inline critical CSS
- [ ] Test with real data
- [ ] Monitor delivery rates

### Content Management
- [ ] Implement content versioning
- [ ] Use slugs for SEO-friendly URLs
- [ ] Add content preview functionality
- [ ] Implement workflow states
- [ ] Cache content aggressively
- [ ] Use CDN for media files
- [ ] Implement search functionality
- [ ] Add content scheduling
- [ ] Track content analytics
- [ ] Regular content audits
- [ ] Implement proper permissions
- [ ] Use structured metadata