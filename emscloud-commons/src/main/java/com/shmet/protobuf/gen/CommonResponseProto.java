package com.shmet.protobuf.gen;

public final class CommonResponseProto {
    private CommonResponseProto() {
    }

    public static void registerAllExtensions(
            com.google.protobuf.ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(
            com.google.protobuf.ExtensionRegistry registry) {
        registerAllExtensions(
                (com.google.protobuf.ExtensionRegistryLite) registry);
    }

    public interface ResponseDataOrBuilder extends
            // @@protoc_insertion_point(interface_extends:ResponseData)
            com.google.protobuf.MessageOrBuilder {
        /**
         * <code>sint32 status = 1;</code>
         */
        int getStatus();

        /**
         * <code>sint32 projectId = 2;</code>
         */
        int getProjectId();
    }

    /**
     * Protobuf type {@code ResponseData}
     */
    public static final class ResponseData extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:ResponseData)
            ResponseDataOrBuilder {
        private static final long serialVersionUID = 0L;

        // Use ResponseData.newBuilder() to construct.
        private ResponseData(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }

        private ResponseData() {
            status_ = 0;
            projectId_ = 0;
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return this.unknownFields;
        }

        private ResponseData(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
                throw new java.lang.NullPointerException();
            }
            int mutable_bitField0_ = 0;
            com.google.protobuf.UnknownFieldSet.Builder unknownFields =
                    com.google.protobuf.UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        case 8: {

                            status_ = input.readSInt32();
                            break;
                        }
                        case 16: {
                            projectId_ = input.readSInt32();
                            break;
                        }
                        default: {
                            if (!parseUnknownFieldProto3(
                                    input, unknownFields, extensionRegistry, tag)) {
                                done = true;
                            }
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                this.unknownFields = unknownFields.build();
                makeExtensionsImmutable();
            }
        }

        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return CommonResponseProto.internal_static_ResponseData_descriptor;
        }

        @java.lang.Override
        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return CommonResponseProto.internal_static_ResponseData_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            CommonResponseProto.ResponseData.class, CommonResponseProto.ResponseData.Builder.class);
        }

        public static final int STATUS_FIELD_NUMBER = 1;
        private int status_;

        /**
         * <code>sint32 status = 1;</code>
         */
        public int getStatus() {
            return status_;
        }

        public static final int PROJECTID_FIELD_NUMBER = 2;
        private int projectId_;

        /**
         * <code>sint32 projectId = 2;</code>
         */
        public int getProjectId() {
            return projectId_;
        }

        private byte memoizedIsInitialized = -1;

        @java.lang.Override
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        @java.lang.Override
        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            if (status_ != 0) {
                output.writeSInt32(1, status_);
            }
            if (projectId_ != 0) {
                output.writeSInt32(2, projectId_);
            }
            unknownFields.writeTo(output);
        }

        @java.lang.Override
        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            if (status_ != 0) {
                size += com.google.protobuf.CodedOutputStream
                        .computeSInt32Size(1, status_);
            }
            if (projectId_ != 0) {
                size += com.google.protobuf.CodedOutputStream
                        .computeSInt32Size(2, projectId_);
            }
            size += unknownFields.getSerializedSize();
            memoizedSize = size;
            return size;
        }

        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof CommonResponseProto.ResponseData)) {
                return super.equals(obj);
            }
            CommonResponseProto.ResponseData other = (CommonResponseProto.ResponseData) obj;

            boolean result = true;
            result = result && (getStatus()
                    == other.getStatus());
            result = result && (getProjectId()
                    == other.getProjectId());
            result = result && unknownFields.equals(other.unknownFields);
            return result;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            hash = (37 * hash) + STATUS_FIELD_NUMBER;
            hash = (53 * hash) + getStatus();
            hash = (37 * hash) + PROJECTID_FIELD_NUMBER;
            hash = (53 * hash) + getProjectId();
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static CommonResponseProto.ResponseData parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static CommonResponseProto.ResponseData parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static CommonResponseProto.ResponseData parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static CommonResponseProto.ResponseData parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static CommonResponseProto.ResponseData parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static CommonResponseProto.ResponseData parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static CommonResponseProto.ResponseData parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }

        public static CommonResponseProto.ResponseData parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static CommonResponseProto.ResponseData parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }

        public static CommonResponseProto.ResponseData parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static CommonResponseProto.ResponseData parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }

        public static CommonResponseProto.ResponseData parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        @java.lang.Override
        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(CommonResponseProto.ResponseData prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        @java.lang.Override
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        /**
         * Protobuf type {@code ResponseData}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:ResponseData)
                CommonResponseProto.ResponseDataOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return CommonResponseProto.internal_static_ResponseData_descriptor;
            }

            @java.lang.Override
            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return CommonResponseProto.internal_static_ResponseData_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                CommonResponseProto.ResponseData.class, CommonResponseProto.ResponseData.Builder.class);
            }

            // Construct using CommonResponseProto.ResponseData.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                }
            }

            @java.lang.Override
            public Builder clear() {
                super.clear();
                status_ = 0;

                projectId_ = 0;

                return this;
            }

            @java.lang.Override
            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return CommonResponseProto.internal_static_ResponseData_descriptor;
            }

            @java.lang.Override
            public CommonResponseProto.ResponseData getDefaultInstanceForType() {
                return CommonResponseProto.ResponseData.getDefaultInstance();
            }

            @java.lang.Override
            public CommonResponseProto.ResponseData build() {
                CommonResponseProto.ResponseData result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            @java.lang.Override
            public CommonResponseProto.ResponseData buildPartial() {
                CommonResponseProto.ResponseData result = new CommonResponseProto.ResponseData(this);
                result.status_ = status_;
                result.projectId_ = projectId_;
                onBuilt();
                return result;
            }

            @java.lang.Override
            public Builder clone() {
                return (Builder) super.clone();
            }

            @java.lang.Override
            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    java.lang.Object value) {
                return (Builder) super.setField(field, value);
            }

            @java.lang.Override
            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }

            @java.lang.Override
            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }

            @java.lang.Override
            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, java.lang.Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }

            @java.lang.Override
            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    java.lang.Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }

            @java.lang.Override
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof CommonResponseProto.ResponseData) {
                    return mergeFrom((CommonResponseProto.ResponseData) other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(CommonResponseProto.ResponseData other) {
                if (other == CommonResponseProto.ResponseData.getDefaultInstance()) return this;
                if (other.getStatus() != 0) {
                    setStatus(other.getStatus());
                }
                if (other.getProjectId() != 0) {
                    setProjectId(other.getProjectId());
                }
                this.mergeUnknownFields(other.unknownFields);
                onChanged();
                return this;
            }

            @java.lang.Override
            public final boolean isInitialized() {
                return true;
            }

            @java.lang.Override
            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                CommonResponseProto.ResponseData parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (CommonResponseProto.ResponseData) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            private int status_;

            /**
             * <code>sint32 status = 1;</code>
             */
            public int getStatus() {
                return status_;
            }

            /**
             * <code>sint32 status = 1;</code>
             */
            public Builder setStatus(int value) {

                status_ = value;
                onChanged();
                return this;
            }

            /**
             * <code>sint32 status = 1;</code>
             */
            public Builder clearStatus() {

                status_ = 0;
                onChanged();
                return this;
            }

            private int projectId_;

            /**
             * <code>sint32 projectId = 2;</code>
             */
            public int getProjectId() {
                return projectId_;
            }

            /**
             * <code>sint32 projectId = 2;</code>
             */
            public Builder setProjectId(int value) {

                projectId_ = value;
                onChanged();
                return this;
            }

            /**
             * <code>sint32 projectId = 2;</code>
             */
            public Builder clearProjectId() {

                projectId_ = 0;
                onChanged();
                return this;
            }

            @java.lang.Override
            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return super.setUnknownFieldsProto3(unknownFields);
            }

            @java.lang.Override
            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return super.mergeUnknownFields(unknownFields);
            }


            // @@protoc_insertion_point(builder_scope:ResponseData)
        }

        // @@protoc_insertion_point(class_scope:ResponseData)
        private static final CommonResponseProto.ResponseData DEFAULT_INSTANCE;

        static {
            DEFAULT_INSTANCE = new CommonResponseProto.ResponseData();
        }

        public static CommonResponseProto.ResponseData getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<ResponseData>
                PARSER = new com.google.protobuf.AbstractParser<ResponseData>() {
            @java.lang.Override
            public ResponseData parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new ResponseData(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<ResponseData> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<ResponseData> getParserForType() {
            return PARSER;
        }

        @java.lang.Override
        public CommonResponseProto.ResponseData getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_ResponseData_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_ResponseData_fieldAccessorTable;

    public static com.google.protobuf.Descriptors.FileDescriptor
    getDescriptor() {
        return descriptor;
    }

    private static com.google.protobuf.Descriptors.FileDescriptor
            descriptor;

    static {
        java.lang.String[] descriptorData = {
                "\n\037protofiles/CommonResponse.proto\"1\n\014Res" +
                        "ponseData\022\016\n\006status\030\001 \001(\021\022\021\n\tprojectId\030\002" +
                        " \001(\021B\025B\023CommonResponseProtob\006proto3"
        };
        com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
                new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
                    public com.google.protobuf.ExtensionRegistry assignDescriptors(
                            com.google.protobuf.Descriptors.FileDescriptor root) {
                        descriptor = root;
                        return null;
                    }
                };
        com.google.protobuf.Descriptors.FileDescriptor
                .internalBuildGeneratedFileFrom(descriptorData,
                        new com.google.protobuf.Descriptors.FileDescriptor[]{
                        }, assigner);
        internal_static_ResponseData_descriptor =
                getDescriptor().getMessageTypes().get(0);
        internal_static_ResponseData_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_ResponseData_descriptor,
                new java.lang.String[]{"Status", "ProjectId",});
    }

    // @@protoc_insertion_point(outer_class_scope)
}
